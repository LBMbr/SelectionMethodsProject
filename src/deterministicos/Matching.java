/*
 * UDESC - Universidade do Estado de Santa Catarina
 * Aluno: Lucas Borges de Moraes
 * Curso: Mestrado em Computação Aplicada - MCA 
 * Orientadores: Adriano Fiorese e Rafael Parpinelli
 */
package deterministicos;

import DB.*;
import modelos.*;
import java.util.ArrayList;
import static modelos.Constantes.*;

public class Matching 
{
    /** A quantidade de provedores na base de dados. */
    private int NUM_PROV;
    /** O número identificador da requisiçao. */
    private int NUM_REQ;
    /** A quantidade de PIs na base de dados. */
    private int NUM_PIS;
    /** O número identificador da base de dados utilizada. */
    private int NUM_DB;
    /** Base de dados de provedores usados pelo matching. */
    private Dados_DB base_dados;
    /** Requisicao do cliente usada pelo matching. */
    private Dados_Req requisicao;
    /** Tabela de atendimento dos PIs da base de dados, tomando como base a requisicao. */
    //private boolean[][] tabAtend; // Tamanho: [Num PIs Req][Num prov DB]
    /** Constante de atendimento basico para PIs quantitativos. Range: ]0,1]. */
    private double C1 = 0.9;
    /** Constante de superacao do atendimento basico para PIs quantitativos. Menor que C1. Range: ]0,1[. */
    private double C2 = 1.0 - C1;
    /** Constante de atendimento basico para PIs qualitativos. Range: ]0,1[.*/
    private double C3 = 0.7;
    
    /**
     * 
     * @param NUM_PROV
     * @param NUM_REQ
     * @param NUM_PIS
     * @param NUM_DB 
     */
    public Matching(int NUM_PROV, int NUM_REQ, int NUM_PIS, int NUM_DB)
    {
        if(NUM_PROV <= 0 || NUM_REQ <= 0 || NUM_PIS <= 0 || NUM_DB <= 0)
            throw new IllegalArgumentException("Matching: Todos os parametros devem ser maiores que zero!");
        this.NUM_PROV = NUM_PROV;
        this.NUM_REQ = NUM_REQ;
        this.NUM_PIS = NUM_PIS;
        this.NUM_DB = NUM_DB;
        // Tenta carregar os dados dos arquivos
        this.base_dados = Dados_DB.carregaDados("Dados/Base" + NUM_DB + "/DADOS" + NUM_PROV + "x" + NUM_PIS + ".txt");
        if(this.base_dados == null)
            throw new NullPointerException("Não foi possível obter os dados dos PIs e dos provedores!");
        
        this.requisicao = Dados_Req.carregaDados("Requisicoes/Base" + NUM_DB + "/REQ" + NUM_REQ + "_" + NUM_PIS + ".txt");
        if(this.requisicao == null)
            throw new NullPointerException("Não foi possível obter os dados da requisição do cliente!");
        
        //this.base_dados.imprimeDados(); // DEBUG
        //this.requisicao.imprimeDados(); // DEBUG
        this.base_dados.atualizaDadosDB(this.requisicao);
        //this.requisicao.imprimeDados(); // DEBUG
        //this.base_dados.imprimeDados(); // DEBUG
        // Usa C1, C2, C3 padroes
    }
    
    /**
     * 
     * @param NUM_PROV
     * @param NUM_REQ
     * @param NUM_PIS
     * @param NUM_DB
     * @param C1
     * @param C3 
     */
    public Matching(int NUM_PROV, int NUM_REQ, int NUM_PIS, int NUM_DB, double C1, double C3)
    {
        if(NUM_PROV <= 0 || NUM_REQ <= 0 || NUM_PIS <= 0 || NUM_DB <= 0 || C1 <= 0 || C1 > 1 || C3 <= 0 || C3 >= 1)
            throw new IllegalArgumentException("Matching: Todos os parametros devem ser maiores que zero!");
        this.NUM_PROV = NUM_PROV;
        this.NUM_REQ = NUM_REQ;
        this.NUM_PIS = NUM_PIS;
        this.NUM_DB = NUM_DB;
        // Tenta carregar os dados dos arquivos
        this.base_dados = Dados_DB.carregaDados("Dados/Base" + NUM_DB + "/DADOS" + NUM_PROV + "x" + NUM_PIS + ".txt");
        if(this.base_dados == null)
            throw new NullPointerException("Não foi possível obter os dados dos PIs e dos provedores!");
        
        this.requisicao = Dados_Req.carregaDados("Requisicoes/Base" + NUM_DB + "/REQ" + NUM_REQ + "_" + NUM_PIS + ".txt");
        if(this.requisicao == null)
            throw new NullPointerException("Não foi possível obter os dados da requisição do cliente!");
        
        //this.base_dados.imprimeDados(); // DEBUG
        //this.requisicao.imprimeDados(); // DEBUG
        this.base_dados.atualizaDadosDB(this.requisicao);
        //this.requisicao.imprimeDados(); // DEBUG
        //this.base_dados.imprimeDados(); // DEBUG
        // Constantes/parametros do matching
        this.C1 = C1;
        this.C2 = 1.0 - C1; // C1 + C2 = 1, obrigatoriamente
        this.C3 = C3;
        
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
    // SETTERS
    public void setC1(double C1)
    {
        if(C1 > 0 && C1 <= 1)
        {
            this.C1 = C1;
            this.C2 = 1.0 - C1;
        }
    }
    public void setC3(double C3)
    {
        if(C3 > 0 && C3 < 1)
            this.C3 = C3;
    }
    
    /**
     * Metodo matematico do PI matching.
     * @param n Quantidade máxima de provedores finais mais bem pontuados pelo matching.
     * @return Um array ordenado de forma decrescente com os "n" provedores mais bem pontuados pelo matching.
     */
    public ArrayList<Provedor> rodaMatching(int n)
    {
        int i, j, pos, numProvs = base_dados.getNumProvedores();
        int numPisReq = requisicao.getNumPis();
        long tempoInicial, tempoFinal;
        double score, peso, menorPeso, sumPesos, maiorCusto;
        Provedor prov;        PI pi, piReq;
        Matching_DadosProvs dados;
        ArrayList<Double> niveis = new ArrayList(); // Pesos diferentes da requisicao
        ArrayList<Integer> countNiveis = new ArrayList(); // Contagem de niveis por peso
        ArrayList<Provedor> candidatos = new ArrayList(); // Provedores iniciais candidatos
        ArrayList<Provedor> eleitos = new ArrayList(); // Provedores compativeis
        ArrayList<String> listaNegra = new ArrayList(); // Nomes dos provedores incompativeis
        ArrayList<Matching_DadosProvs> scoresProvs = new ArrayList(); // Score final e para cada nivel de cada provedor compativel        
        
        System.out.println("# # # # # Algoritmo do PI Matching # # # # #\n");
        System.out.println("NumProv: " + NUM_PROV + "\nNumReq: " + NUM_REQ + "\nNumPIs: " + NUM_PIS + "\nNumDB: " + NUM_DB);
        
        // Liga o cronômetro
        tempoInicial = System.currentTimeMillis(); // Em ms
        //tempoInicial = System.nanoTime();          // Em nanosegundos
        
        // # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # #
        // # # # # # # # # # # # # # OPERAÇÕES  INICIAIS # # # # # # # # # # # #
        // # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # #
        // Ajusta a base inicial de dados
        for(i = 0; i < numProvs; i++)
            candidatos.add(base_dados.getProvedorPos(i));
                
        // Remove todo PI de cada provedor que nao esta presente na requisicao
        removePisExcedentes(candidatos);
        
        // Contagem de quantos PIs por nivel (mesmo peso)
        sumPesos = 0.0;
        menorPeso = Double.MAX_VALUE; // O peso implicito do PI custo
        for(i = 0; i < numPisReq; i++)
        {
            pi = requisicao.getPiPos(i);
            peso = pi.getPeso();
            if(!contem(niveis, peso))
            {   // Peso de valor diferente
                niveis.add(peso);
                sumPesos += peso; // Soma o peso de cada nivel diferente
                countNiveis.add(1); // Inicial o contador                
                if(peso < menorPeso)
                    menorPeso = peso; // Atualiza o menor
            }
            else
            {   // Peso ja adicionado no vetor
                pos = getPosElem(niveis, peso);
                countNiveis.set(pos, countNiveis.get(pos) + 1); // Incremento
            }            
        }
        // Incremento do peso implicito do custo
        for(i = 0; i < niveis.size(); i++)
            if(niveis.get(i) == menorPeso)
                countNiveis.set(i, countNiveis.get(i) + 1);
        
        // DEBUG        
        //atualizaTabAtend();   imprime();
        /*for(i = 0; i < candidatos.size(); i++)
            System.out.println(candidatos.get(i));
        System.out.println();*/
        /*for(i = 0; i < niveis.size(); i++)
            System.out.println("Peso " + niveis.get(i) + ": " + countNiveis.get(i));
        System.out.println("Soma pesos de niveis diferentes: " + sumPesos + "\nMenor peso: " + menorPeso);*/
        
        // # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # #
        // # # # # # # # # # # # # # # #  ETAPA 1  # # # # # # # # # # # # # # #
        // # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # #
        // Eliminação de provedores incompatíveis (não atendem PIs essenciais)        
        for(i = 0; i < numPisReq; i++)
        {   // Para cada PI da requisição
            pi = requisicao.getPiPos(i); 
            if(pi.isEssencial())
            {   // É um PI essencial - Deve ser atendido
                //System.out.println("PI: " + pi.getNome() + " = " + pi.getValor());
                for(j = 0; j < numProvs; j++)
                {   // Para cada provedor disponível no DB
                    prov = candidatos.get(j);
                    //System.out.print("Provedor " + prov.getNome() + ": ");
                    // Atende o PI?
                    if(pi.atende(prov.getPI(pi.getNome()).getValor()))
                    {
                        //System.out.println("Atende");
                    }
                    else
                    {   // Não atende - Provedor incompatível
                        //System.out.println("Não Atende");
                        // Se esse provedor ja nao esta marcado entao insere
                        if(!contem(listaNegra, prov.getNome()))
                            listaNegra.add(prov.getNome());
                    }
                }
            }
        }
        //System.out.println();
        
        // Adiciona os provedores do DB que nao estao na "Lista Negra" ao array de eleitos
        for(i = 0; i < numProvs; i++)
        {
            prov = candidatos.get(i);
            if(!contem(listaNegra, prov.getNome()))
            {   // Adiciona o provedor e zera todos os seus scores (final e por nivel)
                eleitos.add(prov);
                scoresProvs.add(new Matching_DadosProvs(prov.getNome(), niveis.size()));                
            }
        }
        // DEBUG
        /*System.out.println("Provedores compativeis:");
        for(i = 0; i < eleitos.size(); i++)
            System.out.println(eleitos.get(i).getNome());
        System.out.println();*/
        
        // Quantos provedores sobraram?
        if(eleitos.isEmpty())
        {   // Nenhum
            System.out.println("Não há provedores compativeis!");
            return eleitos;
        }
        else if(eleitos.size() == 1)
        {  // Somente 1: não há o que selecionar
           System.out.println("Há somente um provedor compativel!");
           return eleitos;
        }
        
        // Guarda o maior custo da lista de provedores compativeis
        maiorCusto = custoMax(eleitos);
                       
        // Sobraram mais de 1 provedor: Continua o processo        
        // # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # #
        // # # # # # # # # # # # # # # #  ETAPA 2  # # # # # # # # # # # # # # #
        // # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # #
        // Pontuação de PIs quantitativos e qualitativos por nível
        for(i = 0; i < eleitos.size(); i++)
        {   // Para cada provedor compativel
            prov = eleitos.get(i);
            //System.out.println("Provedor " + prov.getNome() + ":");
            for(j = 0; j < numPisReq; j++)
            {   // Para cada PI quantitativo da requisição
                piReq = requisicao.getPiPos(j);
                peso = piReq.getPeso();
                pos = getPosElem(niveis, peso);
                //System.out.print("\t" + piReq.getNome() + ", peso " + peso);
                // Pontuação
                if(!piReq.getNome().equals("custo"))                
                    score = scorePI(prov.getPiPos(j), piReq.getValor());
                else  // Score explicito do custo (informado na requisicao)
                    score = scoreCusto(prov.getCusto(), Double.parseDouble(piReq.getValor()), piReq.getTol(), custoMin(eleitos));                    
                
                getElemProv(scoresProvs, prov).addScoreNivel(pos, score);
                //System.out.println("\t(Pontos: " + score + ")(pos " + pos + ")");
            }
            // Score implicito do custo: 1 - custo Provedor / custo maximo
            pos = getPosElem(niveis, menorPeso);
            score = 1.0 - prov.getCusto() / maiorCusto;
            getElemProv(scoresProvs, prov).addScoreNivel(pos, score);
            //System.out.println("\tCUSTO, peso " + menorPeso + "\t(Pontos: " + score + ")(pos " + pos + ")");
        }
        //System.out.println();
        
        // Tira a média aritmetica por nível
        for(i = 0; i < eleitos.size(); i++)
        {   // Para cada provedor compativel
            prov = eleitos.get(i);
            dados = getElemProv(scoresProvs, prov);
            //System.out.println("Provedor " + prov.getNome() + ":");
            for(j = 0; j < niveis.size(); j++)
            {   // Para cada nível de importancia
                dados.setScoreNivel(j, dados.getScoreNivel(j) / countNiveis.get(j));
                dados.setScoreNivel(j, arredonda(dados.getScoreNivel(j), 4));  // Arredonda com 4 casas decimais
                //System.out.println("\t" + niveis.get(j) + ": " + dados.getScoreNivel(j));
            }
            //System.out.println();
        }
        //System.out.println();
        // # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # #
        // # # # # # # # # # # # # # # #  ETAPA 3  # # # # # # # # # # # # # # #
        // # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # #
        // Pontuação final: Media ponderada de cada nivel
        for(i = 0; i < eleitos.size(); i++)
        {   // Para cada provedor compatível
            prov = eleitos.get(i);
            dados = getElemProv(scoresProvs, prov);
            //System.out.println("Provedor " + prov.getNome() + ":");            
            for(j = 0; j < niveis.size(); j++)
            {   // Para cada nível de importancia
                //System.out.print(dados.getScoreFinal() + " -> ");
                dados.addScoreFinal(niveis.get(j) * dados.getScoreNivel(j));
                //System.out.println("(" + niveis.get(j) + ")[" + i + "]" + " * (" + dados.getScoreNivel(j) + ")[" + j + "]: " + dados.getScoreFinal());
            }
            dados.setScoreFinal(dados.getScoreFinal() / sumPesos);
            dados.setScoreFinal(arredonda(dados.getScoreFinal(), 4)); // Arredonda com 4 casas decimais
            //System.out.println("Score final:" + dados.getScoreFinal() + " pts");
        }      
        
        // # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # #
        // # # # # # # # # # # # # #  OPERAÇÕES FINAIS # # # # # # # # # # # # #
        // # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # #        
        // Ordena os provedores pela pontuação final
        eleitos = ordenaScore(eleitos, scoresProvs);
        // DEBUG
        /*for(i = 0; i < eleitos.size(); i++)
            System.out.println(eleitos.get(i).getNome() + ": " + scoresProvs.get(getPosElem(scoresProvs, eleitos.get(i))).getScoreFinal());*/
                
        // Retorna os "n" melhores provedores
        while(eleitos.size() > n)
            eleitos.remove(eleitos.size() - 1); // Remove o último
        
        // Para o cronômetro
        tempoFinal = (System.currentTimeMillis() - tempoInicial); //Em ms
        System.out.println("O método executou em " + tempoFinal + " ms\n");
        //tempoFinal = (System.nanoTime() - tempoInicial); //Em nanosegundos
        //System.out.println("O método executou em " + ((double)tempoFinal/1000000) + " ms\n");
        
        return eleitos;
    }
    
    /*************************** Funções Utilizadas ***************************/
    /**
     * Remove todo PI de cada provedor que nao esta presente na requisicao.
     * @param provedores Lista de provedores compativeis.
     */
    private void removePisExcedentes(ArrayList<Provedor> provedores)
    {
        PI pi;
        Provedor prov;
        for(int i = 0; i < provedores.size(); i++)
        {
            prov = provedores.get(i);
            for(int j = 0; j < prov.getNumPis(); j++)
            {
                pi = prov.getPiPos(j);
                if(!requisicao.piExiste(pi.getNome()))
                {
                    prov.removePI(pi.getNome());
                    if(j > 0) j = j - 2;
                    else      j--;
                }
            }
        }
    }
    /**
     * Função que pontua o PI quantitativo custo de valor "x" conforme o valor desejado "y".
     * Somente utilizada se o PI custo for informado na requisição.
     * @param x O PI a ser pontuado (o que se tem).
     * @param y Valor desejável para o PI (o que se quer).
     * @param tol A tolerancia dada ao valor do custo.
     * @param min O menor custo dentre os provedores compativeis.
     * @return A pontuação do custo. Normalizada entre 0 e 1.
     */
    public final double scoreCusto(double x, double y, double tol, double min)
    {
        if(PI_Num.atende(x, y, LB, tol))
        {
            //System.out.println(Custo + " atendido com " + x + " (" + y + ")");
            if(min < y)
                return (C1 + C2 * ( (double)(y - x) / (double)(y - min) ) ); // Pontuação [0, 1]
            return 1.00; // Possui valor minimo
        }
        return 0.00; // Nao atende - Pontuação minima
    }
    /**
     * Função que pontua um PI quantitativo conforme o valor desejado "y".
     * @param pi O PI a ser pontuado (o que se tem).
     * @param y Valor desejável para o PI (o que se quer).
     * @return A pontuação do PI. Normalizada entre 0 e 1.
     */
    /*public final double scorePI(PI pi, double y)
    {        
        if(pi.atende("" + y))
        {   // PI atendido
            //System.out.println(pi + " atendido com " + pi.getValor() + " (" + y + ")");
            double x = Double.parseDouble(pi.getValor());
            switch(pi.getTipo())
            {
                case HB:
                    double max = valorMax(pi);
                    if(max > y)
                        return (C1 + C2 * ( (double)(x - y) / (double)(max - y) ) ); // Pontuação [0, 1]
                    return 1.00; // Possui valor maximo
                case LB:
                    double min = valorMin(pi);
                    if(min < y)
                        return (C1 + C2 * ( (double)(y - x) / (double)(y - min) ) ); // Pontuação [0, 1]
                    return 1.00; // Possui valor minimo
                case NB:
                    double tol = pi.getTol();
                    if(x == y)
                        return 1.00; // Pontuação maxima
                    if(tol > 0.00)
                        return (C1 + C2 * ( (double)(tol - Math.abs(y - x)) / tol) ); // Pontuação [0, 1]
                    return 0.00; // Sem tolerancia e x != y - Pontuação minima
                default:
                    return 0.00; // Tipo desconhecido
            }
        }
        return 0.00; // Nao atende - Pontuação minima
    }*/
    /**
     * Função que pontua um PI conforme o valor desejado "y".
     * @param pi O PI a ser pontuado (o que se tem).
     * @param y Valor desejável para o PI (o que se quer).
     * @return A pontuação do PI. Normalizada entre 0 e 1.
     */
    public final double scorePI(PI pi, String y)
    {
        if(pi.atende(y))
        {   // PI atendido
            //System.out.println(pi + " atendido com " + pi.getValorPI() + " (" + y + ")");
            switch(pi.getTYPE())
            {   // Score depende do tipo do PI
                case NUMERICO:
                    PI_Num pi_num = (PI_Num) pi;
                    double x = Double.parseDouble(pi.getValor());
                    double yy = Double.parseDouble(y);
                    switch(pi_num.getTipo())
                    {
                        case HB:
                            double max = valorMax(pi);
                            if(max > x)
                                return (C1 + C2 * ( (double)(x - yy) / (double)(max - yy) ) ); // Pontuação [0, 1]
                            return 1.00; // Possui valor maximo
                        case LB:
                            double min = valorMin(pi);
                            if(min < x)
                                return (C1 + C2 * ( (double)(yy - x) / (double)(yy - min) ) ); // Pontuação [0, 1]
                            return 1.00; // Possui valor minimo
                        case NB:
                            double tol = pi_num.getTol();
                            if(pi_num.getValor().equals(y))
                                return 1.00; // Pontuação maxima
                            if(tol > 0.00)
                                return (C1 + C2 * ( (double)(tol - Math.abs(yy - x)) / tol) ); // Pontuação [0, 1]
                            return 0.00; // Sem tolerancia e x != y - Pontuação minima
                        default:
                            return 0.00; // Tipo desconhecido
                    }                    
                case CAT_SIMP:
                    PI_Cat pi_cat = (PI_Cat) pi;
                    if(pi_cat.isOrdenado()) // Ordenado
                    {
                        // niveis das categorias em questao
                        int niv_x = pi_cat.nivelCat();
                        int niv_y = pi_cat.nivelCat(y); 
                        // Categorias acima de y
                        int k1 = pi_cat.getNumCat() - niv_y;  // ex:(x, b, y, c, d, e) r: 3 
                        // Categorias abaixo de y             // ex:(x, b, y, c, d, e) r: 2    
                        int k2 = niv_y - 1;
                        // Mesmo nivel, mesmo valor
                        if(niv_x == niv_y) 
                            return 1.00; // Pontuação maxima

                        switch(pi_cat.getTipo())
                        {
                            case NB:
                                return 1.00; // Pontuação maxima
                            case HT:
                                return C3 * ((double)(k1 - Math.abs(niv_x - niv_y) + 1.0) / (double)k1); // Pontuação [0, 1]
                            case LT:
                                return C3 * ((double)(k2 - Math.abs(niv_x - niv_y) + 1.0) / (double)k2); // Pontuação [0, 1]
                            case HLT:
                                return C3 * ((double)(k1 + k2 - Math.abs(niv_x - niv_y) + 1.0) / (double)(k1 + k2)); // Pontuação [0, 1]
                            default:
                                return 0.00; // Tipo desconhecido
                        }
                    } // Nao ordenado
                    return 1.00; // Pontuação maxima
                case CAT_COMP:
                    return 1.00; // Pontuação maxima
                default:
                    return 0.00; // TIPO desconhecido
            }
        }
        return 0.00; // Nao atende - Pontuação minima
    }
    /**     
     * @param provedores Lista de provedores compativeis.
     * @return O maior custo dessa lista de provedores. 
     */
    private static double custoMax(ArrayList<Provedor> provedores)
    {        
        double atual, maior = 0.00;        
        for(int i = 0; i < provedores.size(); i++)
        {
            atual = provedores.get(i).getCusto();
            if(atual > maior)
                maior = atual;            
        }
        return maior;
    }
    /**     
     * @param provedores Lista de provedores compativeis.
     * @return O menor custo dessa lista de provedores. 
     */
    private static double custoMin(ArrayList<Provedor> provedores)
    {        
        double atual, menor = Double.MAX_VALUE;        
        for(int i = 0; i < provedores.size(); i++)
        {
            atual = provedores.get(i).getCusto();
            if(atual < menor)
                menor = atual;            
        }
        return menor;
    }
    
    /**
     * Retorna o maior valor para o PI quantitativo passado.
     * @param pi Um PI quantitativo - Inteiro ou real.
     * @return O maior valor presente no banco de dados para esse PI.
     */
    public final double valorMax(PI pi)
    {
        if(pi.getTYPE() != NUMERICO)
            throw new IllegalArgumentException("Essa função é para PIs quantitativos!");
        
        int i, j;
        double atual, maior = 0.00;        
        for(i = 0; i < base_dados.getNumProvedores(); i++)
        {
            for(j = 0; j < base_dados.getProvedorPos(i).getNumPis(); j++)
            {
                if(base_dados.getProvedorPos(i).getPiPos(j).equals(pi))
                {   // Valor pertence ao PI em questão
                    atual = Double.parseDouble(base_dados.getProvedorPos(i).getPiPos(j).getValor());
                    if(atual > maior)
                        maior = atual;
                }
            }
        }
        return maior;
    }
    /**
     * Retorna o menor valor para o PI quantitativo passado.
     * @param pi Um PI quantitativo - Inteiro ou real.
     * @return O menor valor presente no banco de dados para esse PI.
     */
    public final double valorMin(PI pi)
    {
        if(pi.getTYPE() != NUMERICO)
            throw new IllegalArgumentException("Essa função é para PIs quantitativos!");
        
        int i, j;
        double atual, menor = Double.MAX_VALUE;        
        for(i = 0; i < this.base_dados.getNumProvedores(); i++)
        {
            for(j = 0; j < this.base_dados.getProvedorPos(i).getNumPis(); j++)
            {
                if(this.base_dados.getProvedorPos(i).getPiPos(j).equals(pi))
                {   // Valor pertence ao PI em questão
                    atual = Double.parseDouble(base_dados.getProvedorPos(i).getPiPos(j).getValor());
                    if(atual < menor)
                        menor = atual;
                }
            }            
        }
        return menor;
    }   
    /************************** FUNÇÕES DE ORDENAÇÃO **************************/
    /**
     * Função que ordena um Array de provedores conforme o score final de cada um.
     * @param provedores Array de provedores que se pretende ordenar.
     * @param scores Array de scores dos provedores, com as posicoes correspondentes com o Array de provedores.
     * @return Array de provedores ordenado.
     */
    private static ArrayList<Provedor> ordenaScore(ArrayList<Provedor> provedores, ArrayList<Matching_DadosProvs> scores)
    {
        ArrayList<Provedor> provedoresOrdenados = new ArrayList();
        ArrayList<Double> scoresOrdenados = new ArrayList();
        for(int i = 0; i < provedores.size(); i++)        
            insereScore(provedoresOrdenados, scoresOrdenados, provedores.get(i), scores.get(i).getScoreFinal());
        
        return provedoresOrdenados;
    }    
    /**
     * Lista de prioridade onde os provedores de maior score são inseridos primeiro.
     * @param lista Array que se deseja inserir o novo provedor por ordem de score.
     * @param scores Array com os scores de todos os provedores.
     * @param p Novo provedor a ser inserido por prioridade de score.
     * @param novo Score do provedor p.
     */
    private static void insereScore(ArrayList<Provedor> lista, ArrayList<Double> scores, Provedor p, double novo)
    {
        for(int i = 0; i < lista.size(); i++)
        {
            if(novo > scores.get(i))
            { // Insere na posição especifica
                //System.out.println("Inseriu " + p.getNome() + " na pos " + i + " com score " + novo + " maior que " + scores.get(i));
                lista.add(i, p);
                scores.add(i, novo);                
                return;
            }
        }
        //System.out.println("Inseriu " + p.getNome() + " no fim com score " + novo);
        // Insere no fim normalmente
        lista.add(p); 
        scores.add(novo);
    }    
    
    /************************* FUNÇÕES DE ATUALIZAÇÃO *************************/
    /** Função que que cria e preenche a tabela de atendimento. */
    /*private void atualizaTabAtend()
    {
        int i, j;
        int numPIsReq = this.requisicao.getNumPis();
        int numProvsDB = this.base_dados.getNumProvedores();
        PI piReq, piDB;
        Provedor provDB;
        // tabAtend [Num PIs Req][Num prov DB]
        this.tabAtend = new boolean[numPIsReq][numProvsDB];
        for(i = 0; i < numPIsReq; i++) 
        {
            piReq = this.requisicao.getPiPos(i);
            for(j = 0; j < numProvsDB; j++)
            {
                provDB = this.base_dados.getProvedorPos(j);
                if(!piReq.getNome().equals("custo")) // Caso seja informado na requisicao
                {
                    piDB = provDB.getPI(piReq.getNome());
                    this.tabAtend[i][j] = piDB.atende(piReq.getValor(), piReq.getTol());
                }
                else
                {
                    PI_Num custo = (PI_Num) piReq;
                    this.tabAtend[i][j] = PI_Num.atende(provDB.getCusto(), Double.parseDouble(custo.getValor()), LB, piReq.getTol());
                }
            }
            piReq.setTipo(this.base_dados.getTipoPI(piReq.getNome()));
        }
    }*/
    
    /************************** FUNÇÕES DE IMPRESSÕES **************************/
    /** Imprime os dados da tabela de atendimento. */
    /*public void imprimeTabAtend()
    {
        int i, j;    
        System.out.println("Tabela de atendimento:"); 
        System.out.print("\t");
        for(i = 0; i < this.base_dados.getNumProvedores(); i++)        
            System.out.print(this.base_dados.getProvedorPos(i).getNome() + "\t");
        System.out.println();
        for(i = 0; i < this.tabAtend.length; i++)
        {
            System.out.print(this.requisicao.getPiPos(i).getNome() + "\t");
            for(j = 0; j < this.tabAtend[i].length; j++)
                System.out.print((this.tabAtend[i][j] == true ? "X" : ".") + "\t");
            System.out.println();
        }
        System.out.println();
    }*/
    /** Imprime a base de dados utilizada, a requisição e a tabela de atendimento. */
    public void imprime()
    {
        this.base_dados.imprimeDados();
        this.requisicao.imprimeDados(); 
        //this.imprimeTabAtend();
    }
    /************************** FUNÇÕES AUXILIARES *****************************/
        
    private static boolean contem(ArrayList<Double> array, double a)
    {
        for(int i = 0; i < array.size(); i++)
            if(array.get(i) == a)
                return true;
        return false;
    }
    private static int getPosElem(ArrayList<Double> array, double a)
    {
        for(int i = 0; i < array.size(); i++)
            if(array.get(i) == a)
                return i;
        return -1;
    }
    private static boolean contem(ArrayList<String> array, String a)
    {
        for(int i = 0; i < array.size(); i++)
            if(array.get(i).equals(a))
                return true;
        return false;
    }
    private static int getPosElem(ArrayList<String> array, String a)
    {
        for(int i = 0; i < array.size(); i++)
            if(array.get(i).equals(a))
                return i;
        return -1;
    }    
    private static boolean contem(ArrayList<Matching_DadosProvs> array, Provedor a)
    {
        for(int i = 0; i < array.size(); i++)
            if(array.get(i).getNomeProv().equals(a.getNome()))
                return true;
        return false;
    }
    private static int getPosElem(ArrayList<Matching_DadosProvs> array, Provedor a)
    {
        for(int i = 0; i < array.size(); i++)
            if(array.get(i).getNomeProv().equals(a.getNome()))
                return i;
        return -1;
    }
    private static Matching_DadosProvs getElemProv(ArrayList<Matching_DadosProvs> array, Provedor a)
    {
        for(int i = 0; i < array.size(); i++)
            if(array.get(i).getNomeProv().equals(a.getNome()))
                return array.get(i);
        return null;
    }
}
