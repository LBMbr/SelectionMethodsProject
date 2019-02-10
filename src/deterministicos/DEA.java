/*
 * UDESC - Universidade do Estado de Santa Catarina
 * Aluno: Lucas Borges de Moraes
 * Curso: Mestrado em Computação Aplicada - MCA 
 * Orientadores: Adriano Fiorese e Rafael Parpinelli
 */
package deterministicos;

import DB.*;
import modelos.*;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import static modelos.Constantes.*;

public class DEA
{
    /** A quantidade de provedores na base de dados. */
    private int NUM_PROV;
    /** O número identificador da requisiçao. */
    private int NUM_REQ;
    /** A quantidade de PIs na base de dados. */
    private int NUM_PIS;
    /** O número identificador da base de dados utilizada. */
    private int NUM_DB;
    /** Base de dados de provedores usados. */
    private final Dados_DB base_dados;
    /** Requisicao do cliente usada. */
    private final Dados_Req requisicao;
    /** Valores de inputs e outputs de cada provedor calculado. */
    private final ArrayList<DEA_DadosProvs> dadosProvs = new ArrayList();
    
    /** O número de imputs para o DEA. */
    private static final int NUM_IMPUTS = 2;
    /** O número de outputs para o DEA. */
    private static final int NUM_OUTPUTS = 2;
    /** Quantidade de casas decimais, após a virgula, de arredondamento dos dados. */
    private static final int CASAS_DECIMAIS = 3;
        
    /** Pondera a importância dos RECURSOS do provedor com a requisição do cliente. */
    private static final int ALFA = 1;
    /** Pondera a importância do CUSTO do provedor com a requisição do cliente. */
    private static final int BETA = 1;
    /** Pondera a importância da ADEQUACAO do provedor com a requisição do cliente. */
    private static final int GAMMA = 2;
    /** Pondera a importância das SOBRAS do provedor com a requisição do cliente. */
    private static final int DELTA = 1;
    
    /** Constante usada para pontuar os RECURSOS de PIs qualitativos. Range: ]0,1[.*/
    private double C = 0.7;
    
    /**
     * 
     * @param NUM_PROV
     * @param NUM_REQ
     * @param NUM_PIS
     * @param NUM_DB 
     */
    public DEA(int NUM_PROV, int NUM_REQ, int NUM_PIS, int NUM_DB)
    {
        if(NUM_PROV <= 0 || NUM_REQ <= 0 || NUM_PIS <= 0 || NUM_DB <= 0)
            throw new IllegalArgumentException("Matching: Todos os parametros devem ser maiores que zero!");
        this.NUM_PROV = NUM_PROV;
        this.NUM_REQ = NUM_REQ;
        this.NUM_PIS = NUM_PIS;
        this.NUM_DB = NUM_DB;
        // Tenta carregar os dados dos arquivos
        String nomeArqDB = "Dados/Base" + NUM_DB + "/DADOS" + NUM_PROV + "x" + NUM_PIS + ".txt";
        this.base_dados = Dados_DB.carregaDados(nomeArqDB);
        if(this.base_dados == null)
            throw new NullPointerException("Não foi possível obter os dados dos PIs e dos provedores!");
        
        String nomeArqReq = "Requisicoes/Base" + NUM_DB + "/REQ" + NUM_REQ + "_" + NUM_PIS + ".txt";
        this.requisicao = Dados_Req.carregaDados(nomeArqReq);
        if(this.requisicao == null)
            throw new NullPointerException("Não foi possível obter os dados da requisição do cliente!");
        
        //this.base_dados.imprimeDados(); // DEBUG
        //this.requisicao.imprimeDados(); // DEBUG
        this.base_dados.atualizaDadosDB(this.requisicao);
        //this.requisicao.imprimeDados(); // DEBUG
        //this.base_dados.imprimeDados(); // DEBUG
    }
    /**
     * 
     * @param NUM_PROV
     * @param NUM_REQ
     * @param NUM_PIS
     * @param NUM_DB
     * @param C 
     */
    public DEA(int NUM_PROV, int NUM_REQ, int NUM_PIS, int NUM_DB, double C)
    {
        if(NUM_PROV <= 0 || NUM_REQ <= 0 || NUM_PIS <= 0 || NUM_DB <= 0 || C <= 0 || C >= 1)
            throw new IllegalArgumentException("Matching: Todos os parametros devem ser maiores que zero!");
        this.NUM_PROV = NUM_PROV;
        this.NUM_REQ = NUM_REQ;
        this.NUM_PIS = NUM_PIS;
        this.NUM_DB = NUM_DB;
        // Tenta carregar os dados dos arquivos
        String nomeArqDB = "Dados/Base" + NUM_DB + "/DADOS" + NUM_PROV + "x" + NUM_PIS + ".txt";
        this.base_dados = Dados_DB.carregaDados(nomeArqDB);
        if(this.base_dados == null)
            throw new NullPointerException("Não foi possível obter os dados dos PIs e dos provedores!");
        
        String nomeArqReq = "Requisicoes/Base" + NUM_DB + "/REQ" + NUM_REQ + "_" + NUM_PIS + ".txt";
        this.requisicao = Dados_Req.carregaDados(nomeArqReq);
        if(this.requisicao == null)
            throw new NullPointerException("Não foi possível obter os dados da requisição do cliente!");
        
        this.C = C;
        //this.base_dados.imprimeDados(); // DEBUG
        //this.requisicao.imprimeDados(); // DEBUG
        this.base_dados.atualizaDadosDB(this.requisicao);
        //this.requisicao.imprimeDados(); // DEBUG
        //this.base_dados.imprimeDados(); // DEBUG
    }
    
    // GETTERS
    public int getNUM_PROV()
    {
        return NUM_PROV;
    }
    public int getNUM_REQ()
    {
        return NUM_REQ;
    }
    public int getNUM_PIS()
    {
        return NUM_PIS;
    }
    public int getNUM_DB()
    {
        return NUM_DB;
    }
    public Dados_DB getBase_dados()
    {
        return base_dados;
    }        
    public Dados_Req getRequisicao()
    {
        return requisicao;
    }
    public ArrayList<DEA_DadosProvs> getDadosProvs()
    {
        return dadosProvs;
    }
    public double getC()
    {
        return C;
    }
    // SETTER
    public void setC(double C)
    {
        if(C > 0 && C < 1)
            this.C = C;
    }
    
    /**
     * Metodo matematico do CPS-DEA.
     * @return Um array ordenado de forma decrescente com os "n" provedores mais bem pontuados pelo DEA.
     */
    public ArrayList<Provedor> rodaDEA()
    {
        Provedor prov;
        PI piReq, piDB;
        String nomeArq = "dea/Base" + NUM_DB + "_DADOS" + NUM_PROV + "x" + NUM_PIS + "_REQ" + NUM_REQ + "_" + NUM_PIS + "_SIAD.txt";        
        int i, j, numProvs = base_dados.getNumProvedores();
        long tempoInicial, tempoFinal;
        double max, min, media, div, recursos, adequacao, sobras, peso, somaPesos, dist, custos, custoMax = base_dados.getCustoMax();  
        
        System.out.println("# # # # # Algoritmo do DEA # # # # #\n");
        System.out.println("NumProv: " + NUM_PROV + "\nNumReq: " + NUM_REQ + "\nNumPIs: " + NUM_PIS + "\nNumDB: " + NUM_DB);
        
        // Conversor de inputs e outputs do DEA
        try {
            FileWriter fw = new FileWriter(nomeArq, false);
            BufferedWriter bw = new BufferedWriter(fw);
            
            // Inicia o cronômetro
            tempoInicial = System.currentTimeMillis();
            
            // Escreve a quantidade de DMUs e de imputs e outputs
            bw.append(numProvs + "\t" + NUM_IMPUTS + "\t" + NUM_OUTPUTS);
            bw.newLine();            
            bw.append("DMU\tRecursos\tCusto\tAdequacao\tSobras");
            bw.newLine();
            
            // Para cada provedor
            for(i = 0; i < numProvs; i++)
            {
                prov = base_dados.getProvedorPos(i);
                //System.out.println("# # # # # # # # # # # # # # # # # # # # # # # # #");
                //System.out.println("\tPROVEDOR: " + prov.getNome());
                
                // # # # # # # # # # # # # # # # # # # # # # # # # # # # # # #
                //          Calculo do PRIMEIRO IMPUT da DMU (Recursos)
                // # # # # # # # # # # # # # # # # # # # # # # # # # # # # # #
                //System.out.println("# # # # # RECURSOS # # # # #");
                media = 0.0;
                recursos = 0.0;
                for(j = 0; j < requisicao.getNumPis(); j++)
                {
                    piReq = requisicao.getPiPos(j);
                    piDB = prov.getPI(piReq.getNome());
                    peso = piReq.getPeso();
                    if(piReq.getTYPE() == NUMERICO) // Para quantitativos
                    {
                        max = base_dados.getValorMax(piDB.getNome());
                        min = base_dados.getValorMin(piDB.getNome());
                        //System.out.println("Req(" + piReq.getValor() + "), DB(" + piDB.getValor() + ") -> MAX(" + max + "), MIN(" + min + ")");
                        switch(piReq.getTipo()) 
                        {
                            // % % % % % Para PIs HB % % % % %
                            case HB:
                                // HB: (1 - xij/Max(x1j, ..., xnj)) * (Peso PI_HB)
                                media += (1.0 - (Double.parseDouble(piDB.getValor()) / max)) * peso;
                                //System.out.println("PI[HB]: " + piReq.getNome() + "(Peso: " + peso + "), Divisor: " + max + ", Media: " + media);
                                break;
                            // % % % % % Para PIs LB % % % % %
                            case LB:
                                // LB: (xij/Max(x1j, ..., xnj)) * (Peso PI_LB)
                                media += (Double.parseDouble(piDB.getValor()) / max) * peso;
                                //System.out.println("PI[LB]: " + piReq.getNome() + "(Peso: " + peso + "), Divisor: " + max + ", Media: " + media);
                                break;
                            // % % % % % Para PIs NB % % % % %    
                            case NB:
                                // Numerador: Modulo( xij - Xj ) -> distancia
                                dist = Math.abs(Double.parseDouble(piDB.getValor()) - Double.parseDouble(piReq.getValor()));
                                // Divisor: Max(Max(x1j, ..., xnj), Xj) - Min(Min(x1j, ..., xnj), Xj) -> distancia maxima
                                div = Math.max(max, Double.parseDouble(piReq.getValor())) - Math.min(min, Double.parseDouble(piReq.getValor()));
                                // NB: (distancia normalizada do PI_NB) * (Peso PI_NB)
                                if(div > 0.0)
                                {
                                    media += (dist / div) * peso;
                                    //System.out.println("PI[NB]: " + piReq.getNome() + "(Peso: " + peso + "), Divisor: " + div + ", Media: " + media);
                                }
                                break;
                            default:
                                throw new IllegalArgumentException("Tipo de PI numerico desconhecido (" + piReq.getTipo() + "). Tipos conhecidos: NB, HB e LB.\n");
                        }
                    }
                    else // Para qualitativos
                    {
                        if(piDB.atende(piReq.getValor()))
                        {                            
                            if(piDB.isOrdenado()) // Ordenados
                            {
                                PI_Cat pi_Req = (PI_Cat) piReq;
                                PI_Cat pi_DB = (PI_Cat) piDB;
                                // niveis das categorias em questao
                                int niv_x = pi_DB.nivelCat();
                                int niv_y = pi_DB.nivelCat(pi_Req.getValor());
                                // Mesmo nivel, mesmo valor
                                if(niv_x != niv_y)
                                {
                                    // Atendimento imperfeito
                                    // Categorias acima de y
                                    int k1 = pi_DB.getNumCat() - niv_y;  // ex:(x, b, y, c, d, e) r: 3 
                                    // Categorias abaixo de y            // ex:(x, b, y, c, d, e) r: 2    
                                    int k2 = niv_y - 1;
                                    switch(pi_DB.getTipo())
                                    {
                                        case NB:
                                            media += peso; // Sem tolerancia, aumenta RECURSOS o maximo possivel
                                            break;
                                        case HT:
                                            media += (1.0 - C * ((double)(k1 - Math.abs(niv_x - niv_y) + 1.0) / (double)k1)) * peso;
                                            break;
                                        case LT:
                                            media += (1.0 - C * ((double)(k2 - Math.abs(niv_x - niv_y) + 1.0) / (double)k2)) * peso;
                                            break;
                                        case HLT:
                                            media += (1.0 - C * ((double)(Math.max(k1, k2) - Math.abs(niv_x - niv_y) + 1.0) / (double)Math.max(k1, k2))) * peso;
                                            break;
                                        default:
                                            throw new IllegalArgumentException("Tipo de PI categorico desconhecido (" + piReq.getTipo() + "). Tipos conhecidos: NB, HT, LT e HLT.\n");
                                    }
                                } 
                                // Atendimento perfeito, nao aumenta RECURSOS
                            }
                            // Nao ordenado (Cat. simples ou composto),   Atendimento, nao aumenta RECURSOS                            
                        }
                        else // Nao atendimento
                        {
                            media += peso; // Aumenta RECURSOS o maximo possivel
                        }                        
                    }
                }
                // Tira a media
                somaPesos = requisicao.getSomaPesos();
                if(somaPesos > 0.0)
                {
                    media /= somaPesos;                    
                    recursos = Math.pow(media, ALFA); // Eleva a ALFA
                    //System.out.println("Recursos base: " + media + "\nSoma pesos: " + somaPesos + "\nRecursos finais: " + recursos + "\n");
                    recursos = arredonda(recursos, CASAS_DECIMAIS); // Arredonda
                }
                
                // # # # # # # # # # # # # # # # # # # # # # # # # # # # # # #
                //      Calcula e Escreve o SEGUNDO IMPUT da DMU (Custos)
                // # # # # # # # # # # # # # # # # # # # # # # # # # # # # # #
                //System.out.println("# # # # # CUSTO # # # # #");
                media = prov.getCusto() / custoMax;
                custos = Math.pow(media , BETA); // Eleva a BETA                
                //System.out.println("Custo: " + prov.getCusto() + "\nCusto maximo: " + custoMax + "\nRazao: " + media + "\nRazao final: " + custo + "\n");
                custos = arredonda(custos , CASAS_DECIMAIS); // Arredonda
                
                // # # # # # # # # # # # # # # # # # # # # # # # # # # # # # #
                //          Calcula o PRIMEIRO OUTPUT da DMU (Adequacao)
                // # # # # # # # # # # # # # # # # # # # # # # # # # # # # # #
                //System.out.println("# # # # # ADEQUACAO # # # # #");
                media = 0.0;
                for(j = 0; j < requisicao.getNumPis(); j++)
                {
                    piReq = requisicao.getPiPos(j);
                    piDB = prov.getPI(piReq.getNome());
                    peso = piReq.getPeso();
                    if(piDB.atende(piReq.getValor()))
                    {
                        media += peso;
                        //System.out.println("PI " + piDB.getNome() + "(Peso: " + peso + ") -> Atende!");
                    }
                    else
                    {
                        //System.out.println("PI " + piDB.getNome() + "(Peso: " + peso + ") -> Nao Atende!");
                        if(piReq.isEssencial())
                        {
                            //System.out.println("O PI era essencial, zera a adequacao desse provedor!");
                            media = 0.0;
                            break;
                        }
                    }                        
                }
                media /= somaPesos;
                adequacao = Math.pow(media , GAMMA); // Eleva a GAMMA
                //System.out.println("Adequacao base: " + media + "\nAdequacao final: " + adequacao + "\n");
                adequacao = arredonda(adequacao, CASAS_DECIMAIS); // Arredonda
                
                // # # # # # # # # # # # # # # # # # # # # # # # # # # # # # #
                //          Calcula o SEGUNDO OUTPUT da DMU (Sobras)
                // # # # # # # # # # # # # # # # # # # # # # # # # # # # # # #                
                //System.out.println("# # # # # SOBRAS # # # # #");                
                sobras = 0.0;
                media = 0.0;
                somaPesos = 0.0;
                for(j = 0; j < requisicao.getNumPis(); j++)
                {
                    piReq = requisicao.getPiPos(j);
                    if(piReq.getTYPE() == NUMERICO) // Sobras sao so para PIs quantitativos
                    {
                        piDB = prov.getPI(piReq.getNome());
                        peso = piReq.getPeso();
                        max = base_dados.getValorMax(piDB.getNome());
                        // Numerador: xij - Xj -> Distancia positva
                        dist = Double.parseDouble(piDB.getValor()) - Double.parseDouble(piReq.getValor());
                        // Divisor: Max(PI j DB) - Xj -> Distancia positiva maxima
                        div = (max - Double.parseDouble(piReq.getValor()));
                        switch(piReq.getTipo()) 
                        {
                            // % % % % % Para PIs HB % % % % %
                            case HB:
                                //System.out.println("HB: DB(" + piDB.getValor() + ") - Req(" + piReq.getValor() + ") = " + (piDB.getValor() - piReq.getValor()) );                            
                                somaPesos += peso; // Conta o peso
                                if(piDB.atende(piReq.getValor()))
                                {   // HB: (distNorm PI_HB) * (Peso PI_HB) 
                                    if(div > 0.0)
                                    {
                                        media += (dist / div) * peso;
                                        //System.out.println("PI[HB]: " + piReq.getNome() + "(Peso: " + peso + "), MaxDist: " + div + ", Media: " + media);
                                    }                                
                                }
                                else
                                {   // Nao atende " media += 0 "
                                    //System.out.println("PI[HB]: " + piReq.getNome() + "(Peso: " + peso + ") nao atende!");
                                }                             
                                break;
                            // % % % % % Para PIs LB % % % % %
                            case LB:
                                //System.out.println("LB: Req(" + piReq.getValor() + ") - DB(" + piDB.getValor() + ") = " + (piReq.getValor() - piDB.getValor()) );
                                somaPesos += peso; // Conta o peso
                                if(piDB.atende(piReq.getValor()))
                                {   // LB: (1 - distNorm PI_LB) * (Peso PI_LB)
                                    if(div > 0.0)
                                    {
                                        media += (1.0 - (dist / div)) * peso;
                                        //System.out.println("PI[LB]: " + piReq.getNome() + "(Peso: " + peso + "), MaxDist: " + div + ", Media: " + media);
                                    }                                
                                }
                                else
                                {   // Nao atende " media += 0 "
                                    //System.out.println("PI[LB]: " + piReq.getNome() + "(Peso: " + peso + ") nao atende!");
                                }
                                break;
                            // % % % % % Para PIs NB % % % % %    
                            case NB:
                                // Nao acresenta em nada, nao utilizado no calculo das sobras, nao conta o peso
                                //System.out.println("PI NB " + piReq.getNome());
                                break;
                            default:
                                throw new IllegalArgumentException("Tipo de PI desconhecido (" + piReq.getTipo() + "). Tipos conhecidos: NB, HB e LB.\n");
                        }
                    }                    
                }
                // Tira a media                
                if(somaPesos > 0.0)
                {
                    media /= somaPesos;                    
                    sobras = Math.pow(media, DELTA); // Eleva a DELTA
                    //System.out.println("Sobras base: " + media + "\nSoma pesos: " + somaPesos + "\nSobras finais: " + sobras + "\n");
                    sobras = arredonda(sobras, CASAS_DECIMAIS); // Arredonda
                }
                
                // # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # #
                // Salva e escreve o nome do provedor e todos os seus INPUTS e OUTPUTS calculados
                // # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # #
                //System.out.println(prov.getNome() + "\t" + recursos + "\t" + custos + "\t" + adequacao + "\t" + sobras);
                dadosProvs.add(new DEA_DadosProvs(prov.getNome(), recursos, custos, adequacao, sobras));
                bw.append(prov.getNome() + "\t" + recursos + "\t" + custos + "\t" + adequacao + "\t" + sobras);
                bw.newLine();                
            } // Fim loop provedores
            //System.out.println("# # # # # # # # # # # # # # # # # # # # # # # # #");
            
            // Pára o cronômetro
            tempoFinal = (System.currentTimeMillis() - tempoInicial);
            System.out.println("\nTempo de execução: " + tempoFinal + " ms ou " + (tempoFinal/1000.0) + " seg");
            // Fecha o arquivo e seus escritores
            bw.close(); 
            fw.close();
            
        }
        catch(IOException ex)
        {
            System.err.println("Erro ao abrir arquivo " + nomeArq + ": " + ex.getMessage());            
        }
        // Ranqueamento final dos provedores
        return ranqueiaProvedores();
    }
    
    /**
     * Ranqueia os provedores da base de dados de acordo com o valor de seus INPUTS
     * e OUTPUTS calculados na função "geraArq". A ordem de prioridade é:
     * maior "adequação" > menor "custos" > maior "sobras" > menor "recursos".
     * @return Um array contendo todos os provedores da base de dados ranqueados conforme seus INPUTS e OUTPUTS.
     */
    public ArrayList<Provedor> ranqueiaProvedores()
    {        
        ArrayList<Provedor> ranqueados = new ArrayList();
        
        for(int i = 0; i < base_dados.getNumProvedores(); i++)
            insere(ranqueados, base_dados.getProvedorPos(i));
        
        return ranqueados;
    }      
    /**
     * Lista de prioridade onde os provedores de maior são inseridos de acordo 
     * com a prioridade dos valores de seus INPUTS e OUTPUTS calculados.
     * @param lista Array que se deseja inserir o novo provedor.
     * @param novo Novo provedor a ser inserido por prioridade.  
     */
    private void insere(ArrayList<Provedor> lista, Provedor novo)
    {
        Provedor atual;
        int i, j, tamDados = dadosProvs.size();        
        DEA_DadosProvs dadosProvNovo, dadosProvAtual;
        
        dadosProvNovo = null;
        for(i = 0; i < tamDados; i++)
        {
            if(dadosProvs.get(i).getNomeProv().equals(novo.getNome()))
            {
                dadosProvNovo = dadosProvs.get(i);
                break;
            }
        }        
        if(dadosProvNovo == null)
            throw new IllegalArgumentException("Erro na insercao: Dados do provedor " + novo.getNome() + " nao encontrados!");
        
        for(i = 0; i < lista.size(); i++)
        {
            atual = lista.get(i);
            dadosProvAtual = null;
            for(j = 0; j < tamDados; j++)
            {
                if(dadosProvs.get(j).getNomeProv().equals(atual.getNome()))
                {
                    dadosProvAtual = dadosProvs.get(j);
                    break;
                }
            }
            if(dadosProvAtual == null)
                throw new IllegalArgumentException("Erro na insercao: Dados do provedor " + atual.getNome() + " nao encontrados!");
            
            // Prioridade: MAIOR "adequacao" > MENOR "custos" > MAIOR "sobras" > MENOR "recursos
            if(dadosProvNovo.getAdequacao() > dadosProvAtual.getAdequacao())
            {   // Achou onde inserir, por maior "adequacao"
                lista.add(i, novo);
                return;
            }
            // Empate de "adequacao", desempata pelo MENOR "custos"
            if(dadosProvNovo.getAdequacao() == dadosProvAtual.getAdequacao())
            {
                if(dadosProvNovo.getCustos() < dadosProvAtual.getCustos())
                {   // Achou onde inserir, por menor "custos"
                    lista.add(i, novo);
                    return;
                }
                // Empate de "custos", desempata pela MAIOR "sobras"
                if(dadosProvNovo.getCustos() == dadosProvAtual.getCustos())
                {
                    if(dadosProvNovo.getSobras() > dadosProvAtual.getSobras())
                    {   // Achou onde inserir, por maior "sobras"
                        lista.add(i, novo);
                        return;
                    }
                    // Empate de "sobras", desempata pelo MENOR "recursos"
                    if(dadosProvNovo.getSobras() == dadosProvAtual.getSobras())
                    {
                        if(dadosProvNovo.getRecursos() < dadosProvAtual.getRecursos())
                        {   // Achou onde inserir, por menor "recursos"
                            lista.add(i, novo);
                            return;
                        }
                        // MAIOR ou IQUAL "recursos", continua para a proxima posicao
                    }
                    // MENOR "sobras", continua para a proxima posicao
                }
                // MAIOR "custos", continua para a proxima posicao
            }
            // MENOR "adequacao", continua para a proxima posicao
        }
        lista.add(novo); // Nao inseriu em nenhuma posicao, insere no fim entao
    }
    /** Imprime os dados da base de dados e da requisição carregada. */
    public void imprimeDados()
    {
        base_dados.imprimeDados();
        requisicao.imprimeDados();         
    }
}