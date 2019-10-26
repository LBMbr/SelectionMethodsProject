/*
 * UDESC - Universidade do Estado de Santa Catarina
 * Aluno: Lucas Borges de Moraes
 * Curso: Mestrado em Computação Aplicada - MCA 
 * Orientadores: Adriano Fiorese e Rafael Parpinelli
 */
package aplicacao;

import DB.*;
import modelos.*;
import java.util.ArrayList;
import modelosMetaheuristicas.IndividuoBin;
import modelosMetaheuristicas.IndividuoReal;
import modelosMetaheuristicas.PopulacaoBin;
import static modelos.Constantes.*;
import static java.lang.Math.max;
import static java.lang.Math.abs;

public class AvaliaFitness
{   
    // Informações primordiais para o cálculo do fitness
    private final Dados_DB base_dados; 
    private final Dados_Req requisicao; 
    private double custoTotal;
    private int minProv = 1;
    private double custoMin = 0.00;
    private int maxProv; // Para o problema de escalonamento do número de provedores
    private double maxCusto; // Para o problema de escalonamento do custo
    // Tabela de atendimento dos PIs, utilizada para o cálculo de fitness.
    private boolean[][] tabAtend; // Tamanho: [Num PIs Req][Num prov DB]
        
    /**
     * 
     * @param nomeArqDados
     * @param nomeArqReq 
     */
    public AvaliaFitness(String nomeArqDados, String nomeArqReq) 
    {
        // Tenta carregar os dados dos arquivos.
        this.base_dados = Dados_DB.carregaDados(nomeArqDados);
        if(this.base_dados == null)
            throw new NullPointerException("Não foi possível obter os dados dos PIs e dos provedores!");
        
        this.requisicao = Dados_Req.carregaDados(nomeArqReq);
        if(this.requisicao == null)
            throw new NullPointerException("Não foi possível obter os dados da requisição do cliente!");
        
        //this.base_dados.imprimeDados();   this.requisicao.imprimeDados(); // DEBUG  
        this.base_dados.atualizaDadosDB(this.requisicao);
        //this.base_dados.imprimeDados();   this.requisicao.imprimeDados(); // DEBUG               
        atualizaTabAtend();
        //imprimeTabAtend(); // DEBUG  
        atualizaCustoTotal();
        //System.out.println("Custo total: " + custoTotal); // DEBUG  
        atualizaMinProv();
        //System.out.println("Numero provedores minimos esperados: " + minProv); // DEBUG  
        atualizaMinCusto();
        //System.out.println("Custo minimo esperado: " + custoMin); // DEBUG  
        maxProv = base_dados.getNumProvedores() - minProv;
        maxCusto = custoTotal - custoMin;
        //System.out.println("Max Prov = " + maxProv); // DEBUG  
        //System.out.println("Max Custo = " + maxCusto); // DEBUG  
    }
    
    // GETTERS
    public Dados_DB getBase_dados()
    {
        return base_dados;
    }
    public Dados_Req getRequisicao()
    {
        return requisicao;
    } 
    public double getCustoTotal() 
    {
        return custoTotal;
    }
        
    /** Função que que cria e preenche a tabela de atendimento. */
    private void atualizaTabAtend()
    {         
        int i, j;
        int numPIsReq = requisicao.getNumPis();
        int numProvsDB = base_dados.getNumProvedores();
        PI piReq, piDB;
        Provedor provDB;
        // tabAtend [Num PIs Req][Num prov DB]
        tabAtend = new boolean[numPIsReq][numProvsDB];
        for(i = 0; i < numPIsReq; i++)
        {
            piReq = requisicao.getPiPos(i);
            for(j = 0; j < numProvsDB; j++)
            {
                provDB = base_dados.getProvedorPos(j);
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
        }
    }
    /** Atualiza o custo total dos provedores do banco de dados. */
    private void atualizaCustoTotal()
    { 
        custoTotal = 0.00;
        for(int i = 0; i < base_dados.getNumProvedores(); i++)
            custoTotal += base_dados.getProvedorPos(i).getCusto();
        
        custoTotal = arredonda(custoTotal, 2);
    }
    /** Atualiza a quantidade minima de provedores esperada para a requisição. */
    private void atualizaMinProv()
    {        
        // Tamanho: [Num PIs Req][Num prov DB]
        int i, j, k, count, numPis = tabAtend.length, numProv = tabAtend[0].length;
        boolean[] atendidos = new boolean[numPis];
        // Tem um unico provedor que atende todos os PIs?
        for(i = 0; i < numProv; i++)
        {            
            count = 0;
            for(j = 0; j < numPis; j++)
            {
                if(tabAtend[j][i])
                    count++;
                //System.out.println("(Prov " + (i+1) + ": PI " + (j+1) + ") - " + count);
            }
            //System.out.println("Prov " + (i+1) + " - " + count);
            // O provedor i atendeu todos os PIs j?
            if(count == numPis)
            {   // Atendeu, 1 provedor ja responde
                minProv = 1;                
                return;
            }          
        }
        // Se nao tem um, tem dois provedor que atende todos os PIs?
        for(k = 0; k < numProv - 1; k++) // Testa todas as combinações de 2 em 2
        {
            for(i = k + 1; i < numProv; i++)
            {                
                for(j = 0; j < numPis; j++)
                {
                    atendidos[j] = (tabAtend[j][i] || tabAtend[j][k]);
                    //System.out.println("(Prov " + (k+1) + " Prov " + (i+1) + ": PI " + (j+1) + ") - " + atendidos[j]);
                }
                count = 0;
                for(j = 0; j < numPis; j++)
                {
                    if(atendidos[j])
                        count++;
                }
                //System.out.println("Prov " + (k+1) + ", Prov " + (i+1) + " - " + count);
                // Os provedores k e i atendem todos os PIs j?
                if(count == numPis)
                {   // Atendeu, 2 provedores sao suficientes
                    minProv = 2;                    
                    return;
                } 
            }
        }
        // Tres ou mais provedores sao necessarios
        minProv = 3;                
    }
    /** Atualiza o custo minimo esperado para a requisição. */
    private void atualizaMinCusto()
    {
        int i;
        double aux;
        ArrayList<Double> custos, custosMenores;
        
        custos = new ArrayList();
        for(i = 0; i < base_dados.getNumProvedores(); i++)
            custos.add(base_dados.getProvedorPos(i).getCusto());
        
        custosMenores = new ArrayList();
        while(custosMenores.size() < minProv)
        {
            aux = Double.MAX_VALUE;
            for(i = 0; i < custos.size(); i++)
                if(custos.get(i) < aux)
                    aux = custos.get(i);
            
            custos.remove(aux);
            custosMenores.add(aux);
        }
        custoMin = 0.00;
        for(i = 0; i < custosMenores.size(); i++)
            custoMin += custosMenores.get(i);
        
        custoMin = arredonda(custoMin, 2);
    }         
    
    /**
     * Atualiza os denominadores maximos para o numero de provedores e o custo,
     * conforme a população passada.
     * @param p Populacao binaria para conferir novos denominadores maximos.
     */
    public void atualizaMaximos(PopulacaoBin p)
    {
        int i, j, comMaisProv = p.maxNumProvCod();
        double custo, custoMax = 0.00;
        IndividuoBin ind;
        
        if(comMaisProv < maxProv)
            maxProv = max(comMaisProv, minProv);
        //System.out.println("Max Prov = " + maxProv);
        
        for(i = 0; i < p.getTamPop(); i++)
        {
            ind = p.getIndPos(i);
            custo = 0.00;
            for(j = 0; j < ind.getTamCod(); j++)
            {
                if(ind.getCodPos(j) == true)       
                    custo += base_dados.getProvedorPos(j).getCusto();
            }
            if(custo > custoMax)
                custoMax = custo;
        }
        if(custoMax < maxCusto)
            maxCusto = max(custoMax, custoMin);
        //System.out.println("Max Custo = " + maxCusto);
    }
    /**
     * Reinicia os denominadores maximos do numero de provedores e do custo para
     * os valores padroes dessa base de dados.
     */
    /*public void reiniciaMaximosPadroes()
    {
        maxProv = base_dados.getNumProvedores() - minProv;
        maxCusto = custoTotal - custoMin;
    }*/
    /**
     * Função de avaliação do fitness para o individuo "ind".
     * @param ind IndividuoBin que se deseja avaliar o fitness.
     * @return O fitness do individuo passado.
     */    
    public double fitness(IndividuoBin ind)
    {
        if(ind.getTamCod() > base_dados.getNumProvedores())
            throw new IllegalArgumentException("Erro: A codificação do individuo não deve ultrapassar"
                    + " a quantidade de provedores cadastrados no banco de dados!");
        
        // Mostra o individuo
        //System.out.print("IndividuoBin [" + ind.getId() + "]  Codificação: ");
        //ind.imprimeCod();
        //System.out.println();
        
        // O individuo nulo é invalido
        if(ind.individuoNulo())
        {
            ind.setFitness(0.00);
            ind.setPenalizado(true);
            return 0.00;
        }
        
        double wc = 1.0; // Peso do custo
        double wn = 1.0; // Peso do numero de provedores
        double wpen = 1.0; // Peso da funcao de penalidade
        // Componetes da função objetivo e de fitness
        double custo = Custo(ind.getCod());
        double numPr = numProv(ind.getCod());        
        // Função Objetivo normalizada
        double objetivo = ((wc * custo) + (wn * numPr)) / (wc + wn); 
        // Aplicação de penalidades
        double penalidade = calculaPenalidade(ind.getCod());
        // O indivíduo foi penalizado?        
        if(penalidade == 0.00)  ind.setPenalizado(false);
        else                    ind.setPenalizado(true); 
        // Função de Fitness = Minimiza o objetivo - penalidades
        double fitness = (1.00 - objetivo - (wpen * penalidade));               
        // Fitness final deve ser positivo
        if(fitness < 0.00) fitness = 0.00;   
        // Atualiza o fitness do individuo
        ind.setFitness(fitness);
        // Resultados - DEBUGG 
        //System.out.println("PtsCusto: " + custo);
        //System.out.println("PtsProv: " + numPr);
        //System.out.println("Objetivo: " + objetivo);
        //System.out.println("Penalidade: " + penalidade);
        //System.out.println("Fitness: " + fitness);
        return fitness;
    }
    /**
     * Função de avaliação do fitness para o individuo "ind".
     * @param ind IndividuoReal que se deseja avaliar o fitness.
     * @return O fitness do individuo passado.
     */    
    public double fitness(IndividuoReal ind)
    {
        if(ind.getTamCod() > base_dados.getNumProvedores())
            throw new IllegalArgumentException("Erro: A codificação do individuo não deve ultrapassar"
                    + " a quantidade de provedores cadastrados no banco de dados!");
        
        // Discretiza o valor real
        ind.atualizaCodBin();
        
        // Mostra o individuo
        //System.out.print("IndividuoReal [" + ind.getId() + "]  Codificação: ");
        //ind.imprimeCod();
        //ind.imprimeCodBin();
        //System.out.println();
        
        // O individuo nulo é invalido
        if(ind.individuoNulo())
        {
            ind.setFitness(0.00);
            ind.setPenalizado(true);
            return 0.00;
        }
        
        double wc = 1.0; // Peso do custo
        double wn = 1.0; // Peso do numero de provedores
        double wpen = 1.0; // Peso da funcao de penalidade
        // Componetes da função objetivo e de fitness
        double custo = Custo(ind.getCodBin());
        double numPr = numProv(ind.getCodBin());        
        // Função Objetivo normalizada
        double objetivo = ((wc * custo) + (wn * numPr)) / (wc + wn); 
        // Aplicação de penalidades
        double penalidade = calculaPenalidade(ind.getCodBin());
        // O indivíduo foi penalizado?        
        if(penalidade == 0.00)  ind.setPenalizado(false);
        else                    ind.setPenalizado(true); 
        // Função de Fitness = Minimiza o objetivo - penalidades
        double fitness = (1.00 - objetivo - (wpen * penalidade));               
        // Fitness final deve ser positivo
        if(fitness < 0.00) fitness = 0.00;   
        // Atualiza o fitness do individuo
        ind.setFitness(fitness); 
        // Resultados - DEBUGG 
        //System.out.println("PtsCusto: " + custo);
        //System.out.println("PtsProv: " + numPr);
        //System.out.println("Objetivo: " + objetivo);
        //System.out.println("Penalidade: " + penalidade);
        //System.out.println("Fitness: " + fitness);
        return fitness;
    }
    /**
     * Calcula a pontuação para o número de provedores.
     * @param indCod Vetor binario de codificacao do Individuo.
     * @return Um valor real entre 0 e 1.
     */
    public final double numProv(boolean[] indCod)
    {        
        int numProv = 0;
        for(int i = 0; i < indCod.length; i++)
            if( indCod[i] )
                numProv++;
        
        //System.out.println("Número de provedores: " + numProv);
        if(numProv != 0 && numProv <= maxProv) 
            return abs( (double)(numProv - minProv) / (double) maxProv );
            //return abs( (double)(numProv - minProv) / (double)(ind.getTamCod() - minProv) );            
        else // Casos extremos, indesejáveis.
            return 1.00;
    }    
    /**
     * Calcula a pontução para o custo.
     * @param indCod Vetor binario de codificacao do Individuo.
     * @return Um valor real entre 0 e 1.
     */
    public final double Custo(boolean[] indCod)
    {   
        // O individuo nulo é invalido
        //if(ind.individuoNulo())  return 1.00; 
        
        // Calcula o custo individual de "ind"
        double custo = 0.00;
        for(int i = 0; i < indCod.length; i++)
                if( indCod[i] ) // O custo do provedor entra na contagem            
                    custo += base_dados.getProvedorPos(i).getCusto();        
        // DEBUGG
        //System.out.println("Custo do individuo: " + custo + "\n");
        if(custo <= maxCusto)
            return abs( (custo - custoMin) / maxCusto);
            //return abs( (custo - custoMin) / (custoTotal - custoMin) );
        else
            return 1.00;            
    }        
    /**
     * Retorna a proporção de quantos PIs da requisição não foram atendidos pela 
     * codificação do indivíduo dado.
     * @param indCod Vetor binario de codificacao do Individuo.
     * @return Um valor real entre 0 e 1.
     */
    public final double calculaPenalidade(boolean[] indCod)
    {
        int i, j, k, grupo1, grupo2, tamGrupo, numPIsAtend, numPIsMaisAtend;
        int numPIsReq = requisicao.getNumPis();        
        double penalidade, somaPesos, pesoPI;        
        // Vetor informando como aquele PI foi atendido
        double[] atendeReq = new double[numPIsReq];        
        // *  1 - Não atendido completamente - Penalidade maxima 
        // *  0 - Atendido completamente - Penalidade minima 
        // * (0, 1) - Atendido parcialmente - Para PIs com grupos (ID > 0)
                
        // O individuo nulo é invalido
        //if(ind.individuoNulo())  return 1.00;
        
        // Inicia o vetor
        for(i = 0; i < numPIsReq; i++)        
            atendeReq[i] = -1.00;  // Nao avaliado  
      
        // Confere se a requisição foi toda satisfeita
        // Para cada PI "i" da requisição
        for(i = 0; i < numPIsReq; i++)
        {   // Ainda nao foi avaliado
            if(atendeReq[i] == -1.00)
            {
                grupo1 = requisicao.getPiPos(i).getGrupo();
                // O PI pertence a um grupo?
                if(grupo1 > 0)
                {   // Existe um grupo valido com "tamGrupo" PIs
                    tamGrupo = 0;
                    for(k = i; k < numPIsReq; k++)
                    {
                        grupo2 = requisicao.getPiPos(k).getGrupo();
                        if(grupo1 == grupo2) // O PI pertence ao grupo em questao
                            tamGrupo++;                        
                    }  
                    numPIsMaisAtend = 0;
                    // Para todo provedor "j" da base de dados                            
                    for(j = 0; j < indCod.length; j++) 
                    {   // Esse provedor faz parte da solução (presente na codificação do individuo)                        
                        if( indCod[j] )
                        {
                            numPIsAtend = 0;
                            // Percorre todos os PIs desse grupo
                            for(k = i; k < numPIsReq; k++)
                            {
                                grupo2 = requisicao.getPiPos(k).getGrupo();
                                if(grupo1 == grupo2)
                                {   // Verifica se o provedor "j" atende o PI "k"
                                    if( tabAtend[k][j] )
                                        numPIsAtend++;       
                                }
                            }
                            if(numPIsAtend > numPIsMaisAtend)
                                numPIsMaisAtend = numPIsAtend;                            
                        }
                    }                    
                    //System.out.println("Tamanho do grupo: " + tamGrupo); // DEBUG
                    //System.out.println("Atendidos: " + numPIsMaisAtend);   // DEBUG
                    // Contabiliza os atendimentos parciais dos PIs do grupo
                    for(k = i; k < numPIsReq; k++)
                    {
                        grupo2 = requisicao.getPiPos(k).getGrupo();
                        if(grupo1 == grupo2) // Pontua igualmente cada PI do grupo                        
                            atendeReq[k] = (1.00 - ((double)numPIsMaisAtend / (double)tamGrupo));                        
                    }                    
                }
                else
                {   // Nao possui grupo
                    // Para todo provedor "j" da base de dados                            
                    for(j = 0; j < indCod.length; j++) 
                    {   // Esse provedor faz parte da solução (presente na codificação do individuo)                        
                        if( indCod[j] )
                        {   // Verifica se o provedor "j" atende o PI "i"
                            if( tabAtend[i][j] )     
                            {   // Se 1 provedor atende eh o suficiente
                                atendeReq[i] = 0.00; // Atendido completamente 
                                break;
                            }                            
                        } 
                    }
                    if(atendeReq[i] == -1) // Nenhum provedor atendeu
                        atendeReq[i] = 1.00; // Nao atendido completamente
                }
                // DEBUGS
                /*System.out.println("PI " + this.requisicao.getPiPos(i).getNome());
                for(k = 0; k < numPIsReq; k++)
                    System.out.print(atendeReq[k] + "  ");
                System.out.println();*/
            }
            else
            {
                //System.out.println("PI " + this.requisicao.getPiPos(i).getNome() + " ja foi avaliado!");
            }
            
        }
        // Contabiliza as penalidades e calcula a soma dos pesos na requisição, para normalizacao
        penalidade = 0.00;
        somaPesos = 0.00;
        for(i = 0; i < numPIsReq; i++) 
        {
            pesoPI = requisicao.getPiPos(i).getPeso();
            // DEBUG
            /*if(atendeReq[i] == 0.00)    System.out.println("PI: " + this.requisicao.getPiPos(i).getNome() + " >> Atendido completamete. Peso: " + pesoPI);
            else if(atendeReq[i] == 1.00) System.out.println("!!!PI: " + this.requisicao.getPiPos(i).getNome() + " >> Nao atendido completamete. Peso: " + pesoPI);
            else                          System.out.println("!PI: " + this.requisicao.getPiPos(i).getNome() + " >> Atendido parcialmente (" + atendeReq[i] + "). Peso: " + pesoPI);
            */
            penalidade += atendeReq[i] * pesoPI;
            somaPesos  += pesoPI;
        } 
        // DEBUGG
        //System.out.println("Penalidade acumulada: " + penalidade);
        //System.out.println("Soma dos pesos: " + somaPesos);
        //System.out.println("Penalidade normalizada: " + penalidade / somaPesos + "\n");
        // A penalidade eh proporcionar ao peso dos PIs nao atendidas na requisicao
        return ( penalidade / somaPesos );   
    }
            
    /** Imprime os dados da tabela de atendimento. */
    public void imprimeTabAtend()
    {
        int i, j;    
        System.out.println("Tabela de atendimento:"); 
        System.out.print("\t");
        for(i = 0; i < base_dados.getNumProvedores(); i++)        
            System.out.print(base_dados.getProvedorPos(i).getNome() + "\t");
        System.out.println();
        for(i = 0; i < tabAtend.length; i++)
        {
            System.out.print(requisicao.getPiPos(i).getNome() + "\t");
            for(j = 0; j < tabAtend[i].length; j++)
                System.out.print( (tabAtend[i][j] ? "X" : ".") + "\t");
            System.out.println();
        }
        System.out.println();
    }
    /** Imprime os dados obtidos para calculo do fitness. */
    public void imprime()
    {
        base_dados.imprimeDados();
        requisicao.imprimeDados(); 
        imprimeTabAtend();
    }
}