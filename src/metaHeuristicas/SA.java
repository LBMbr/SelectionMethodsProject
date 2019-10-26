/*
 * UDESC - Universidade do Estado de Santa Catarina
 * Aluno: Lucas Borges de Moraes
 * Curso: Mestrado em Computação Aplicada - MCA 
 * Orientadores: Adriano Fiorese e Rafael Parpinelli
 */
package metaHeuristicas;

import java.util.ArrayList;
import aplicacao.AvaliaFitness;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;
import static modelos.Constantes.*;
import static java.lang.Math.exp;
//import static java.lang.Math.PI;
//import static java.lang.Math.log;
//import static java.lang.Math.cos;
//import static java.lang.Math.cosh;
//import static java.lang.Math.tanh;

/**
 * Implementa o Simulated Anneling (SA).
 * 
 * Pseudocódigo do Simulated Anneling (SA) básico.
 * PARÂMETROS: Numero Maximo de Iterações (It), Constante de Metropolis (MC),
 *   Temperatura Inicial (T0), Temperatura Final (Tf), Funcao de Decaimento (FD)
 *   e a Solucao Inicial(s0). It = MC * numero de decaimentos de temperatura.
 * 
 * candidato = s0;
 * melhor = candidato;
 * T = T0;
 * iteracao = 0;
 * Enquanto T > Tf
 *      Para ItT de 0 ate MC faça
 *          vizinho = geraVizinho(candidato);
 *          deltaE = energia(vizinho) - energia(candidato);
 *          se (deltaE ≤ 0) então
 *              candidato = vizinho
 *              se energia(candidato) < energia(melhor) então
 *                  melhor = candidato
 *              fim se
 *          senão faça
 *              p = exp(-deltaE/T);
 *              x = Random(0,1);
 *              se x < p então
 *                  candidato = vizinho;
 *              fim se;
 *          fim se;
 *      fim para;
 *      T = FD(T);
 * fim enquanto;
 * retorna melhor; 
 */
public class SA 
{
    /** A quantidade de provedores na base de dados. */
    private int NUM_PROV;    
    /** O número identificador da requisiçao. */
    private int NUM_REQ;    
    /** A quantidade de PIs na base de dados. */
    private int NUM_PIS;
    /** O número identificador da base de dados utilizada. */
    private int NUM_DB;
    /** O número maximo de execucoes do SA. */
    private int MAX_EXECUCOES;
    /** O número maximo de iteracoes do SA. */
    private int IT_MAX;
    /** A constante de Metropolis do SA: Quantidade de iteracoes com a mesma temperatura. */
    private int IT_M;
    /** A quantidade total de decaimentos de temperatura. */
    private int IT_T;    
    /** A temperatura inicial do SA (padrao 1). */
    private double T0 = 1.0;
    /** A temperatura final do SA (padrao 0). */
    private double Tf = 0.0;
    /** Flag definindo se havera output em arquivo dos dados das iteracoes do SA ou não (false). */
    private boolean RELATORIO;
    /** Gerador randomico da classe. */
    private static final Random gerador = new Random();
    
    /**
     * Implementa o algoritmo do Simulated Anneling utilizando as configuracoes
     * padroes para a temperatura inicial (1) e final (0).
     * @param NUM_PROV Numero total de provedores da base de dados.
     * @param NUM_REQ Numero identificador da requisiçao do cliente.
     * @param NUM_PIS Numero total de PIs na base de dados.
     * @param NUM_DB Numero identificador da base de dados utilizada.
     * @param MAX_EXECUCOES Numero maximo de execucoes do SA.
     * @param IT_MAX Numero maximo de iteracoes do SA.
     * @param cteM Contante de Metropolis, i.e., numero de iteracoes para a mesma temperatura.
     */
    public SA(int NUM_PROV, int NUM_REQ, int NUM_PIS, int NUM_DB, int MAX_EXECUCOES, int IT_MAX, int cteM)
    {
        if(NUM_PROV <= 0 || NUM_REQ <= 0 || NUM_PIS <= 0 || NUM_DB <= 0 || MAX_EXECUCOES <= 0 || IT_MAX <= 0 || cteM <= 0)
            throw new IllegalArgumentException("SA: Todos os parametros devem ser maiores que zero!");
        this.NUM_PROV = NUM_PROV;
        this.NUM_REQ = NUM_REQ;
        this.NUM_PIS = NUM_PIS;
        this.NUM_DB = NUM_DB;
        this.MAX_EXECUCOES = MAX_EXECUCOES;
        this.IT_MAX = IT_MAX;
        this.IT_M = cteM;        
        // Total de iteracoes (IT_MAX) = Iteracoes de Temp.(It_T) * Iteracoes daquela Temp.(IT_M)
        // Logo,  It_T = IT_MAX / IT_M
        this.IT_T = IT_MAX / IT_M;
    }
    // GETTERS
    /** @return O numero total de provedores da base de dados. */
    public int getNUM_PROV() 
    {
        return NUM_PROV;
    }
    /** @return O numero identificador da requisiçao do cliente. */
    public int getNUM_REQ() 
    {
        return NUM_REQ;
    }
    /** @return O numero total de PIs na base de dados. */
    public int getNUM_PIS()
    {
        return NUM_PIS;
    }
    /** @return O numero identificador da base de dados utilizada. */
    public int getNUM_DB() 
    {
        return NUM_DB;
    }
    /** @return O numero maximo de execucoes do SA. */
    public int getMAX_EXECUCOES() 
    {
        return MAX_EXECUCOES;
    }
    /** @return O número maximo de iteracoes do SA. */
    public int getIT_MAX()
    {
        return IT_MAX;
    }
    /** @return A quantidade de iteracoes com a mesma temperatura (Cte de Metropolis). */
    public int getIT_M()
    {
        return IT_M;
    }
    /** @return A quantidade total de decaimentos de temperatura. */
    public int getIT_T()
    {
        return IT_T;
    }
    /** @return A temperatura inicial do SA. */
    public double getT0()
    {
        return T0;
    }
    /** @return A temperatura final do SA. */
    public double getTf()
    {
        return Tf;
    }       
    /** @return A flag indicando se ha output em arquivo dos dados das geracoes desse algoritmo não. */
    public boolean isRELATORIO()
    {
        return RELATORIO;
    }
    
    /**
     * Roda o Algoritmo Genetico de busca de fitness.
     * @param RELATORIO Flag definindo se havera output em arquivo dos dados das geracoes do AG ou não (false).
     * @param s0 A solucao candidata inicial. Envie nulo para a solucao inicial ser aleatoria.
     * @return A melhor solucao encontrada pelo SA, a partir da solucao inicial, s0.
     */
    public ArrayList<boolean[]> rodaSA(boolean RELATORIO, boolean[] s0)
    {  
        this.RELATORIO = RELATORIO;
        // Carrega os dados
        String ArqBD = "Dados/Base" + NUM_DB + "/DADOS" + NUM_PROV + "x" + NUM_PIS + ".txt";
        String ArqReq = "Requisicoes/Base" + NUM_DB + "/REQ" + NUM_REQ + "_" + NUM_PIS + ".txt";
        AvaliaFitness af = new AvaliaFitness(ArqBD, ArqReq);
        //af.imprime();        
        //af.imprimeTabAtend();
        
        // Variaveis
        int i, it, itT, itDec, execucao, numProvs = af.getBase_dados().getNumProvedores();
        double T, prob, Ec, Ev, deltaE, Em;
        long tempoInicial, tempoFinal;        
        boolean[] candidato, vizinho, melhor;        
        boolean[][] eleitos = new boolean[MAX_EXECUCOES][numProvs]; // Melhores solucoes de cada execusão
        double[] energias = new double[MAX_EXECUCOES]; // Melhores energias
        double[] tempoExecusao = new double[MAX_EXECUCOES]; // Tempo de execusão de cada execusão
        boolean aleatorio = (s0 == null || s0.length != numProvs); // Solucao inicial do metodo
        // Para relatorio
        FileWriter fwE, fwC;
        BufferedWriter bwE = null, bwC = null;        
        
        System.out.println("# # # # # Simulated Anneling # # # # #\n");        
        //System.out.println("Espaço de busca: " + (Math.pow(2, numProvs) - 1));
        System.out.println("NumProv: " + NUM_PROV + "\nNumReq: " + NUM_REQ + "\nNumPIs: " + NUM_PIS + "\nNumDB: " + NUM_DB);
                
        // DEBUG
        /*System.out.println("\nO numero total de execucoes: " + MAX_EXECUCOES);
        System.out.println("O número maximo de iteracoes (IT_MAX): " + IT_MAX);
        System.out.println("A quantidade de iteracoes com a mesma temperatura (IT_M): " + IT_M);
        System.out.println("A quantidade total de decaimentos de temperatura (IT_T): " + IT_T);
        System.out.println("A temperatura inicial (T0): " + T0);
        System.out.println("A temperatura final (Tf): " + Tf);
        System.out.println("A solucao inicial (s0): ");  imprimeVet(s0);
        System.out.println();*/
                
        // Inicia as execuções
        execucao = 1;        
        // LOOP DE EXECUÇÕES DO SA
        while(execucao <= MAX_EXECUCOES)
        {   // Inicio do SA
            //System.out.println("\nExecucao: " + execucao);
            // Inicia o cronômetro
            tempoInicial = System.currentTimeMillis();            
            
            if(aleatorio)
            {   // s0 sera aleatorio
                s0 = new boolean[numProvs];
                for(i = 0; i < numProvs; i++)
                    s0[i] = gerador.nextBoolean();
                //System.out.print("Execucao: " + execucao + " usa s0 aleatorio!");
            }
            else
            {
                //System.out.println("Execucao: " + execucao + " usa s0 dado pelo usuario!");
                //imprimeVet(s0);
            }
            // Iniciando as iteracoes, temperaturas e a solucao inicial
            it = 0; // Iteracoes totais
            itDec = 0; // Iteracoes de decaimento de temperatura
            T = T0;
            candidato = s0;
            // O melhor encontrado e a energia do melhor
            melhor = candidato;
            Em = energia(af, melhor);            
            if(RELATORIO)
            {                
                try {
                    /*fwC = new FileWriter("temps/candidatosSA" + execucao + ".txt");
                    bwC = new BufferedWriter(fwC);
                    for(i = 0; i < (candidato.length - 1); i++)
                        bwC.append(candidato[i] ? "1 ":"0 ");
                    bwC.append(candidato[i] ? "1":"0");
                    bwC.newLine();
                    bwC.append(provCod(candidato));
                    bwC.newLine();
                    */
                    fwE = new FileWriter("temps/energiasSA" + execucao + ".txt");
                    bwE = new BufferedWriter(fwE);                    
                    bwE.append(arredonda(Em, 8) + ",");
                }
                catch(IOException ex)
                {
                    System.out.println("Erro ao escrever no arquivo: " + ex.getMessage());                
                }
            }
            //System.out.println("\nTemperatura inicial = " + T);
            // Enquanto nao atingir a temperatura final: 1 .. 0
            while(T > Tf)
            {   // Iteracoes para essa mesma temperatura
                for(itT = 0; itT < IT_M; itT++)
                {
                    vizinho = geraVizinho(candidato);
                    // DEBUG
                    //System.out.print("Candidato: ");  imprimeVet(candidato);
                    //System.out.print("Novo vizinho: ");  imprimeVet(vizinho);
                    // Calcula as energias do candidato (Ec) e do vizinho (Ev)
                    Ec = energia(af, candidato);
                    Ev = energia(af, vizinho);
                    deltaE = Ev - Ec;
                    // DEBUG                    
                    //System.out.println("Energia do candidato (Ec): " + Ec);
                    //System.out.println("Energia do vizinho (Ev): " + Ev);
                    //System.out.println("Variacao de energia (deltaE): " + deltaE);
                    if(deltaE <= 0.00)
                    {   // Aceita o proximo 
                        candidato = vizinho;
                        Ec = Ev;
                        //System.out.println("Aceitou o vizinho!");
                        // Eh o melhor de todos? Menor energia?
                        if(Ec < Em)
                        {
                            Em = Ec;
                            melhor = vizinho;
                            //System.out.println("Atualizou o melhor!");
                        }
                    }
                    else
                    {   // Aceita com uma probabilidade: e ^ (- deltaE / (k * T) )
                        // k = Constante de Boltzmann = 1 (padrao)
                        prob = exp(- deltaE / T);
                        //System.out.println("Prob (T = " + T + "): " + prob);
                        // Estocasticidade
                        if(gerador.nextDouble() < prob)
                        {   // Aceita o proximo                            
                            candidato = vizinho;
                            Ec = Ev;
                            //System.out.println("Aceitou o vizinho pior!");
                        }
                        else
                        {
                            //System.out.println("Rejeitou o vizinho!");
                        }
                    }
                    it++;
                    if(RELATORIO)
                    {                        
                        try {  
                            /*for(i = 0; i < (candidato.length - 1); i++)
                                bwC.append(candidato[i] ? "1 ":"0 ");
                            bwC.append(candidato[i] ? "1":"0");
                            bwC.newLine();
                            bwC.append(provCod(candidato));
                            bwC.newLine();*/
                            
                            bwE.append( arredonda(Ec, 8) + (it >= IT_MAX ? "":",") );                            
                        } catch(IOException ex) {
                            System.out.println("Erro ao escrever no arquivo: " + ex.getMessage());                
                        }
                    }
                }
                //System.out.println();
                // Decaimento da temperatura
                itDec++;
                T = arredonda(decTemp(itDec), 15);                
                //System.out.println("Nova temperatura: " + T);
            } // Fim SA
            // Pára o cronômetro
            tempoFinal = (System.currentTimeMillis() - tempoInicial);
            // DEBUG
            //System.out.print("\nCandidato final: ");  imprimeVet(candidato);            
                        
            // Guarda o melhor individuo, sua energia e o tempo dessa execução
            eleitos[execucao - 1] = melhor;
            energias[execucao - 1] = Em;
            tempoExecusao[execucao - 1] = tempoFinal; 
            // DEBUG
            //System.out.println("\nExecução " + execucao + ": ");            
            //System.out.print("Melhor solucao: ");  imprimeVet(melhor);
            //System.out.println("Melhor energia: " + Em);
            //System.out.println("Tempo da execução " + execucao + ": " + tempoFinal + " ms ou " + (tempoFinal/1000.0) + " seg");
            if(RELATORIO)
            {   // Fecha arquivos abertos
                try {
                //bwC.close();
                bwE.close();                
                } catch (IOException ex) {
                    System.out.println("Erro ao fechar arquivo de energias: " + ex.getMessage());  
                }
            }            
            
            // Próxima execucao do SA
            execucao++;
        } // Fim das execucoes
        // DEBUG
        //System.out.println(); for(i = 0; i < MAX_EXECUCOES; i++) imprimeVet(eleitos[i]);
        
        // # # # # Estatisticas pós execuções do SA # # # #
        return estatisticaFinais(eleitos, energias, tempoExecusao);        
    } // FIM
    /**************************************************************************/
    /********************** DECREMENTO DE TEMPERATURA *************************/
    /**************************************************************************/
    /**
     * Funcao do decremento de temperatura a cada iteracao passada.
     * @param it Numero da iteracao de decremento de temperatura.
     * @return O valor da nova temperatura na iteracao passada.
     */
    public final double decTemp(double it)
    {
            // Linear (nao eh muito boa)
            //return (T0 - it * ((T0 - Tf) / IT_T));
            // Exponecial decrescente suave (Inadequada Tf = 0 -> 0 ^ xxx = 1)
            //return (T0 * pow(Double.MIN_VALUE / T0, (double) it / IT_T)); xx
        // Exponecial decrescente abrupta (boa)
        double A = (double)((T0 - Tf) * (IT_T + 1)) / IT_T;   return (A /(it + 1) + T0 - A);
            // Logaritmica decrescente suave (BUGGA!) da zero
            //double A = log(T0 - Tf) / log(IT_T);   return (T0 - pow(it, A)); xx
            // Exponecial com picos e vales longos (nao eh muito boa)
            //return ((T0 - Tf) / (1 + exp(3 * (it - IT_T/2)) ) + Tf);
            // Cosenoide decrescente (nao eh muito boa)
            //return (0.5 * (T0 - Tf) * (1 + cos(it * PI/IT_T)) + Tf);
            // Tangente hiperbolica decrescente com longos picos e vales (nao eh muito boa)
        //return (0.5 * (T0 - Tf) * (1 - tanh((10 * it/IT_T) - 5)) + Tf);
        // Coseno hiperbolica decrescente (boa)
        //return ((T0 - Tf) / cosh(10 * it/IT_T) + Tf);
            // Logaritmica decrescente suave (nao utilizavel, divisao por zero)
            //double A = (1/N) * log(t0/tn);   return (t0 * exp( -A * i));
    }
    /**
     * Funcao para estudo do decremento de temperatura a cada iteracao passada.
     * @param i Numero da iteracao de decremento de temperatura.
     * @param t0 Temperatura inicial do sistema.
     * @param tn Temperatura final do sistema.
     * @param N Numero total de decaimentos de temperatura.
     * @return O valor da nova temperatura na iteracao passada.
     */
    public final static double decTemp(double i, double t0, double tn, int N)
    {
            // Linear (nao eh muito boa)
            //return (t0 - i * ((t0 - tn) / N));
            // Exponecial decrescente suave (Inadequada Tn = 0 -> 0 ^ xxx = 1)
            //return (t0 * pow(Double.MIN_VALUE / t0, (double) i / N));
        // Exponecial decrescente abrupta (boa)
        double A = (double)((t0 - tn) * (N + 1)) / N;   return (A /(i + 1) + t0 - A);
            // Logaritmica decrescente suave (BUGGA!) da zero
            //double A = log(t0 - tn) / log(N);   return (t0 - pow(i, A));
            // Exponecial com picos e vales longos (nao eh muito boa)
            //return ((t0 - tn) / (1 + exp(3 * (i - N/2)) ) + tn);
            // Cosenoide decrescente (nao eh muito boa)
            //return (0.5 * (t0 - tn) * (1 + cos(i * PI/N)) + tn);
            // Tangente hiperbolica decrescente com longos picos e vales (nao eh muito boa)
            //return (0.5 * (t0 - tn) * (1 - tanh((10 * i/N) - 5)) + tn);
        // Coseno hiperbolica decrescente (boa)
        //return ((t0 - tn) / cosh(10 * i/N) + tn);
            // Logaritmica decrescente suave (nao utilizavel, divisao por zero)
            //double A = (1/N) * log(t0/tn);   return (t0 * exp( -A * i));
    } // FIM
    
    /**************************************************************************/
    /********************** ESTATISTICAS E RELATORIOS *************************/
    /**************************************************************************/
    /**
     * Gera as estatisticas finais pós execuções do SA, como energia e tempo medio,
     * desvio padrao de energia e tempo e a porcentagem de acertos.
     * @param eleitos Vetor com todas as solucoes eleitas pelo SA a cada execusao.
     * @param energias Vetor com todas as energias das as solucoes eleitas pelo SA a cada execusao.
     * @param tempoExecusao Vetor com todos os tempos de execusao das de cada execusao.
     * @return A(s) melhor(es) solucao(oes) encontrada(s) em todas as execucoes.
     */
    private ArrayList<boolean[]> estatisticaFinais(boolean[][] eleitos, double[] energias, double[] tempoExecusao)
    {   
        ArrayList<boolean[]> melhores = new ArrayList(); // Os melhores das execusoes para a requisição
        int i, totalExe = eleitos.length;
        double acertos = 0.00;        
        double mediaE = 0.00;
        double mediaT = 0.00;
        double desvioE = 0.00;
        //double desvioT = 0.00;
        double melhorE = Double.MAX_VALUE; // A melhor (menor) energia conhecida no momento para a requisição
        
        // Medias de egergias e tempo e a melhor (menor) energia encontrado
        for(i = 0; i < totalExe; i++)
        {
            mediaE += energias[i];            
            mediaT += tempoExecusao[i];
            if(energias[i] < melhorE)
                melhorE = energias[i];            
        }
        mediaE = mediaE / totalExe;        
        mediaT = mediaT / totalExe;  
               
        // Conta os acertos e acha os melhores
        for(i = 0; i < totalExe; i++)
        {
            if(energias[i] == melhorE)
            {
                acertos++;
                if(!contem(melhores, eleitos[i]))
                    melhores.add(eleitos[i]); // BUG AQUI!
            }
        }        
        acertos = (acertos / totalExe) * 100; // Em %
        acertos = arredonda(acertos, 2); // Arredonda o valor dos acertos
        // Desvio padrao das energias e tempo
        for(i = 0; i < totalExe; i++)
        {
            desvioE += Math.pow(mediaE - energias[i], 2);
            //desvioT += Math.pow(mediaT - tempoExecusao[i], 2);
        }
        desvioE = desvioE / (totalExe - 1);
        desvioE = Math.sqrt(desvioE);
        desvioE = arredonda(desvioE, 10); // Arredonda o valor do desvio
        mediaE = arredonda(mediaE, 10); // Arredonda o valor da media
        //desvioT = desvioT / (totalExe - 1);        
        //desvioT = Math.sqrt(desvioT);       
        
        // Impressão dos resultados estatisticos em arquivo e na tela
        FileWriter fw;
        BufferedWriter bw;            
        try {
            fw = new FileWriter("EstatisticasSA.txt", true);
            bw = new BufferedWriter(fw);
            // No arquivo: "EstatisticasSA.txt"
            // Escreve: (Num req)(Num prov)(Num pis) <TAB> MEDIA energia <TAB> DESVIO PADRAO energia <TAB> ACERTOS (%) <TAB> Menor energia <TAB> TEMPO medio (ms)
            bw.append("(Req " + NUM_REQ + ")(Provs " + NUM_PROV + ")(PIs " + NUM_PIS + ")\t" + 
                mediaE + "\t" + desvioE + "\t" + acertos + "%" + "\t" + melhorE + "\t" + arredonda(mediaT, 2));
            bw.newLine();
            bw.close();
            ////////////////////////////////////////////////////////////////////
            fw = new FileWriter("EstatisticasExecucoesSA.txt", true);            
            bw = new BufferedWriter(fw);            
            // No arquivo: "EstatisticasExecucoesSA.txt"
            // Escreve: (Num req)(Num prov)(Num pis)
            bw.append("(Req " + NUM_REQ + ")(Provs " + NUM_PROV + ")(PIs " + NUM_PIS + ")");
            bw.newLine();
            // Escreve de cada execução: Menor energia, tempo e as respostas
            bw.append("energia <- c(" + arredonda(energias[0], 8));
            for(i = 1; i < totalExe; i++)
                bw.append(", " + arredonda(energias[i], 8));
            bw.append(");");
            bw.newLine();
            bw.append("tempos <- c(" + tempoExecusao[0]);
            for(i = 1; i < totalExe; i++)
                bw.append(", " + tempoExecusao[i]);
            bw.append(");");
            bw.newLine();
            bw.append("Respostas:");
            bw.newLine();
            for(i = 0; i < totalExe; i++)
            {
                bw.append(provCod(eleitos[i]));
                bw.newLine();
            }
            bw.newLine();                                   
            bw.close();
        }
        catch(IOException ex)
        {
            System.out.println("Erro ao escrever no arquivo de estatisticas do SA: " + ex.getMessage());
        }
        
        System.out.println("\nMedia ENERGIA: " + mediaE + "\nDesvio padrão ENERGIA: " + desvioE);
        System.out.println("\nMedia TEMPO: " + arredonda(mediaT, 2) + " ms ou " + (arredonda(mediaT/1000.0, 4)) + " seg");
        //System.out.println("Desvio padrão TEMPO: " + arredonda(desvioT, 5) + " ms ou " + (arredonda(desvioT/1000.0, 5)) + " seg");
        System.out.println("Acertos = " + acertos + "%");
        /*System.out.println("Melhor(es) provedor(es) das " + totalExe + " execusoes:");
        for(i = 0; i < melhores.size(); i++)
        {
            //imprimeVet(melhores.get(i));
            imprimeProvCod(melhores.get(i));
        }*/
        System.out.println();
        // Junta todos os dados armazenados em um unico arquivo de relatorio
        if(RELATORIO)
        {
            System.out.println("Preparando dados finais para relatorio...");
            juntaDadosSA(melhorE);
            System.out.println("Feito!\n");
        }
        return melhores;
    }
    
    /**
     * Agrega todos os dados do SA em um único arquivo e prapara-os como entrada no R.
     * Imprime ao fim do arquivo de dados os plots da média das execuções a serem
     * calculadas no R.
     * @param menorEnergia Menor energia obtida de todas as execuções.
     */
    private void juntaDadosSA(double menorEnergia)
    {        
        try {   
            int i, j;
            double temp;
            FileWriter fw = new FileWriter("logs/DadosSA" + NUM_PROV + "x" + NUM_REQ + ".txt", false);            
            BufferedWriter bw = new BufferedWriter(fw);
            FileReader fr;
            BufferedReader br;
            
            for(i = 1; i <= MAX_EXECUCOES; i++)
            {                
                fr = new FileReader("temps/energiasSA" + i + ".txt");
                br = new BufferedReader(fr);
                bw.append("energiasSA" + i + " <- c(" + br.readLine() + ");"); 
                bw.newLine();
                br.close();
                fr.close();                
            }
            // Pos processamentos finais
            String energias = "energiasSA <- (";
            for(i = 1; i < MAX_EXECUCOES; i++)                            
                energias += "energiasSA" + i + " + ";            
            energias += "energiasSA" + MAX_EXECUCOES + ")/" + MAX_EXECUCOES + ";";
            bw.append(energias);
            bw.newLine();
            /*
            String temperaturas = "temperaturasSA <- c(";
            for(i = 0; i < IT_T; i++)
            {
                temp = arredonda(decTemp(i), 15);
                for(j = 0; j < IT_M; j++)
                    temperaturas += temp + ",";
            }
            temp = arredonda(decTemp(i), 15);            
            temperaturas += temp + ");";
            bw.append(temperaturas);
            bw.newLine();
            */
            bw.append("iteracoesSA <- c(seq(0, " + IT_MAX + "));");
            bw.newLine();
            bw.newLine();
            // Normalizacao dos valores no R
            //bw.append("maxSA <- max(energiasSA);");
            //bw.append("maxSA <- " + arredonda(menorEnergia, 4) + ";");
            //bw.newLine();
            //bw.append("minSA <- min(energiasSA);");
            //bw.newLine();
            //bw.append("energiasNormSA <- ((1.0/(maxSA - minSA)) * energiasSA - (1.0/(maxSA - minSA)) * minSA);");
            //bw.newLine();            
            //bw.newLine();
            // Função plot no R
            // Carregar arquivo: source("..//Desktop//DadosEDD.txt");
            // plot(vetor de dados, type="<p/i/o>", col="<COR>", xlab="<nome eixo x>", xlim/ylim = c(.., ..)
            //   ylab="<nome eixo y>", main="<Título>", sub = "<Subtitulo>")
            // lines(dados, type = "p/i/o", col = "COR") -> Adiciona uma curva
            // https://docs.ufpr.br/~aanjos/CE231/web/apostila.html
            // http://www.w3ii.com/pt/r/r_line_graphs.html
            // VARS: geracoes, fitMelhor, fitMedio e variabilidade          
            // SALVAR imagem: pdf("nome.pdf"); ou jpeg("nome.jpg"); ou png("nome.jpg");
            //                  comando para o grafico
            //                dev.off();
            bw.append("pdf(\"../Desktop/SA_Energia" + NUM_PROV + "_Req" + NUM_REQ + ".pdf\");");
            bw.newLine(); // energiasNormSA
            bw.append("plot(energiasSA ~ iteracoesSA, type='l', col='blue', "
                    + "xlab='Iterations', ylab='Energy', ylim=c(0,0.5), "
                    + "main='Energy of the mean of " + MAX_EXECUCOES + " executions (SA)');");            
            bw.newLine();            
            bw.append("dev.off();");
            bw.newLine();
            bw.newLine();
            /*bw.append("pdf(\"../Desktop/SA_Temp" + NUM_PROV + "_Req" + NUM_REQ + ".pdf\");");
            bw.newLine();
            bw.append("plot(temperaturasSA ~ iteracoesSA, type='l', col='blue', xlab='Iterations', "
                    + "ylab='Temperature', ylim=c(0,1), "
                    + " main='SA temperature curve');");
            bw.newLine(); 
            bw.append("dev.off();");
            bw.newLine();*/
            bw.close();
        }
        catch (IOException ex)
        {
            System.err.println("juntaDadosSA: Erro com os arquivos: " + ex.getMessage());
        }
    }
    
    /************************* FUNÇÕES DO PROBLEMA ****************************/
    /**
     * Gera um vetor booleano 'vizinho' ao passado por paramentro, isso eh,
     * perturbado em um bit. Nao deve gerar vetor todo preenchido com 0s.
     * @param candidato Elemento corrente.
     * @return O 'vizinho' do 'candidato'.
     */
    private static boolean[] geraVizinho(boolean[] candidato)
    {
        int i, flag, randPos, umPos;
        boolean[] novo = null;
        
        if(candidato != null)
        {
            novo = new boolean[candidato.length];
            flag = 0; umPos = -1;
            for(i = 0; i < novo.length; i++)
            {
                novo[i] = candidato[i];
                if(novo[i] == false) flag++;
                else                 umPos = i;
            }
            if(flag == candidato.length - 1) // Possui um unico valor com 1 
            {
                do {
                    randPos = gerador.nextInt(candidato.length);
                } while(randPos == umPos); // Caso que gera o vetor todo com zeros
            }
            else  randPos = gerador.nextInt(candidato.length);
            
            novo[randPos] = ! novo[randPos]; // Bit flip
        }
        return novo;
    }
    /**
     * Calcula a energia dessa solucao, baseada na base de dados e da requisicao. 
     * @param af
     * @param candidato
     * @return A energia desse candidato. Quanto menor melhor.
     */
    public static double energia(AvaliaFitness af, boolean[] candidato)
    {
        if(af == null || candidato == null)
            throw new IllegalArgumentException("Energia: Erro! Entrada invalida!");
                
        // Componentes da função de energia (Custo, Numero de provedores, Funcao de penalidade)
        double custo = af.Custo(candidato);
        double numPr = af.numProv(candidato);
        double request = af.calculaPenalidade(candidato);
        // Pesos das componentes
        double wc = 1.0;
        double wn = 1.0;
        double wreq = 1.0;
        // Função de energia normalizada
        //double energia = (((wc * custo) + (wn * numPr)) / (wc + wn)) + (wreq * request);
        double energia = ((((wc * custo) + (wn * numPr)) / (wc + wn)) + (wreq * request)) / (1 + wreq); 
        
        // Resultados - DEBUG 
        //System.out.print("Solucao: ");  imprimeVet(candidato);
        //System.out.println("PtsCusto: " + custo);
        //System.out.println("PtsProv: " + numPr);
        //System.out.println("Penalidade: " + request);
        //System.out.println("Energia: " + energia);
        return energia;
    }        
}
