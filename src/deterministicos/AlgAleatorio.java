/*
 * UDESC - Universidade do Estado de Santa Catarina
 * Aluno: Lucas Borges de Moraes
 * Curso: Mestrado em Computação Aplicada - MCA 
 * Orientadores: Adriano Fiorese e Rafael Parpinelli
 */
package deterministicos;

import aplicacao.AvaliaFitness;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import modelosMetaheuristicas.IndividuoBin;
import static modelos.Constantes.*;

public class AlgAleatorio 
{
    /** A quantidade de provedores na base de dados. */
    private int NUM_PROV;    
    /** O número identificador da requisiçao. */
    private int NUM_REQ;    
    /** A quantidade de PIs na base de dados. */
    private int NUM_PIS;
    /** O número identificador da base de dados utilizada. */
    private int NUM_DB;
    /** O número maximo de execucoes do algoritmo. */
    private int MAX_EXECUCOES;
    /** O número maximo de iteracoes do algoritmo. */
    private int MAX_ITERACOES;    
    /**
     * Implementa o algoritmo aleatorio de busca de fitness.
     * @param NUM_PROV O numero total de provedores da base de dados.
     * @param NUM_REQ O numero identificador da requisiçao do cliente.
     * @param NUM_PIS O numero total de PIs na base de dados.
     * @param NUM_DB O numero identificador da base de dados utilizada.
     * @param MAX_EXECUCOES O numero maximo de execucoes do algoritmo.
     * @param MAX_ITERACOES O numero maximo de iteracoes do algoritmo.
     */
    public AlgAleatorio(int NUM_PROV, int NUM_REQ, int NUM_PIS, int NUM_DB, int MAX_EXECUCOES, int MAX_ITERACOES)
    {
        if(NUM_PROV <= 0 || NUM_REQ <= 0 || NUM_PIS <= 0 || NUM_DB <= 0 || MAX_EXECUCOES <= 0 || MAX_ITERACOES <= 0)
            throw new IllegalArgumentException("AlgAleatorio: Todos os parametros devem ser maiores que zero!");
        this.NUM_PROV = NUM_PROV;
        this.NUM_REQ = NUM_REQ;
        this.NUM_PIS = NUM_PIS;
        this.NUM_DB = NUM_DB;
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
    /** @return O numero maximo de execucoes do algoritmo. */
    public int getMAX_EXECUCOES() 
    {
        return MAX_EXECUCOES;
    }
    /** @return O numero maximo de iteracoes do algoritmo. */
    public int getMAX_ITERACOES() 
    {
        return MAX_ITERACOES;
    }
    
    // SETTERS
    /**
     * Modifica o identificador da quantidade de provedores na base de dados.
     * @param NUM_PROV O novo valor do identificador da quantidade de provedores na base de dados.
     */
    public void setNUM_PROV(int NUM_PROV) 
    {
        if(NUM_PROV > 0)
            this.NUM_PROV = NUM_PROV;
    }
    /**
     * Modifica o identificador da requisiçao do cliente.
     * @param NUM_REQ O novo valor do identificador da requisiçao do cliente.
     */
    public void setNUM_REQ(int NUM_REQ) 
    {
        if(NUM_REQ > 0)
            this.NUM_REQ = NUM_REQ;
    }
    /**
     * Modifica o identificador da quantidade de PIs da base de dados.
     * @param NUM_PIS O novo valor do identificador da quantidade de PIs da base de dados.
     */
    public void setNUM_PIS(int NUM_PIS) 
    {
        if(NUM_PIS > 0)
            this.NUM_PIS = NUM_PIS;
    }
    /**
     * Modifica o identificador da base de dados utilizada.
     * @param NUM_DB O novo valor do identificador da base de dados utilizada.
     */
    public void setNUM_DB(int NUM_DB) 
    {
        if(NUM_DB > 0)
            this.NUM_DB = NUM_DB;
    }
    /**
     * Modifica o numero maximo de execucoes.
     * @param MAX_EXECUCOES O novo valor para o numero maximo de execucoes.
     */
    public void setMAX_EXECUCOES(int MAX_EXECUCOES)
    {
        if(MAX_EXECUCOES > 0)
            this.MAX_EXECUCOES = MAX_EXECUCOES;
    }
    /**
     * Modifica o numero maximo de iteracoes.
     * @param MAX_ITERACOES O novo valor para o numero maximo de iteracoes.
     */
    public void setMAX_ITERACOES(int MAX_ITERACOES) 
    {
        if(MAX_ITERACOES > 0)
            this.MAX_ITERACOES = MAX_ITERACOES;
    }    
    /** Roda o Algoritmo Aleatorio de busca de fitness. */
    public void rodaAleatorio()
    {
        // Carrega os dados
        String ArqBD = "Dados/Base" + NUM_DB + "/DADOS" + NUM_PROV + "x" + NUM_PIS + ".txt";
        String ArqReq = "Requisicoes/Base" + NUM_DB + "/REQ" + NUM_REQ + "_" + NUM_PIS + ".txt";
        AvaliaFitness af = new AvaliaFitness(ArqBD, ArqReq);
        //af.imprime();
       
        // Variaveis
        int i, numIt, NUM_VARS = af.getBase_dados().getNumProvedores();
        long tempoInicial, tempoFinal;
        IndividuoBin atual;
        IndividuoBin melhor; 
        IndividuoBin[] eleitos = new IndividuoBin[MAX_EXECUCOES]; 
        double[] tempoExecusao = new double[MAX_EXECUCOES];
       
        System.out.println("# # # # # Algoritmo de Busca Aleatoria # # # # #\n");        
        //System.out.println("Espaço de busca: " + (long)(Math.pow(2, NUM_VARS) - 1));
        System.out.println("NumProv: " + NUM_PROV + "\nNumReq: " + NUM_REQ + "\nNumPIs: " + NUM_PIS + "\nNumDB: " + NUM_DB);
        
        // Inicia as execuções
        int execucao = 1;
        // LOOP DE EXECUÇÕES
        while(execucao <= MAX_EXECUCOES)
        {
            numIt = 0;            
            melhor = new IndividuoBin(NUM_VARS);
            // Inicia o cronômetro
            tempoInicial = System.currentTimeMillis(); 
            // LOOP DE ITERAÇÕES
            while(numIt < MAX_ITERACOES)
            {
                atual = new IndividuoBin(NUM_VARS);
                atual.geraCodRand();
                //atual.imprimeCodificacao();
                af.fitness(atual);
                if(atual.getFitness() > melhor.getFitness())
                    melhor = atual;
                
                numIt++;
            }
            // Interrompe o cronômetro
            tempoFinal = (System.currentTimeMillis() - tempoInicial);
            //System.out.println("\nTempo de execução: " + tempoFinal + " ms ou " + (tempoFinal/1000.0) + " seg\n");
            
            // Guarda o melhor individuo e o tempo dessa execução
            eleitos[execucao - 1] = melhor;
            tempoExecusao[execucao - 1] = tempoFinal;
            
            // Imprime o melhor individuo
            System.out.println("\nExecução " + execucao + ": ");
            // Resposta
            if(!melhor.isPenalizado())
            {
                System.out.println("Melhor fitness: " + melhor.getFitness());
                //System.out.print("Codificação:  ");
                //melhor.imprimeCod();
                melhor.imprimeProvCod();                
            }
            else
            {
                System.out.println("Não foi encontrado um individuo valido com " + MAX_ITERACOES + " iterações."); 
                System.out.println("Melhor fitness: " + melhor.getFitness());
                //System.out.print("Codificação:  ");
                //melhor.imprimeCod();
                melhor.imprimeProvCod();                 
            }
            
            //System.out.println("\nTempo da execução " + execucao + ": " + tempoFinal + " ms ou " + (tempoFinal/1000.0) + " seg");
            // Próxima execucao
            execucao++;
        } // Fim das execucoes
        
        // # # # # Estatisticas pós execuções do Aleatorio # # # #
        estatisticaFinais(eleitos, tempoExecusao);
    } // FIM
    
    /**
     * Gera as estatisticas finais pós execuções do Aleatorio, como fitness e tempo medio,
     * desvio padrao de fitness e tempo e a porcentagem de acertos.
     * @param eleitos O vetor com todos os invididuos eleitos pelo Aleatorio a cada execusao.
     * @param tempoExecusao O vetor com todos os tempos de execusao das de cada execusao.
     */
    private void estatisticaFinais(IndividuoBin[] eleitos, double[] tempoExecusao)
    {   
        ArrayList<IndividuoBin> melhores = new ArrayList(); // Os melhores das execusoes para a requisição
        int i, totalExe = eleitos.length;
        double acertos = 0.00;        
        double mediaF = 0.00;
        double mediaT = 0.00;
        double desvioF = 0.00;
        //double desvioT = 0.00;
        double melhorFit = 0.00; // O Melhor fitness conhecido no momento para a requisição
        
        // Medias de fitness e tempo e o melhor fitness encontrado
        for(i = 0; i < totalExe; i++)
        {
            mediaF += eleitos[i].getFitness();            
            mediaT += tempoExecusao[i];
            if(eleitos[i].getFitness() > melhorFit)
                melhorFit = eleitos[i].getFitness();            
        }
        mediaF = mediaF / totalExe;        
        mediaT = mediaT / totalExe;  
               
        // Conta os acertos e acha os melhores
        for(i = 0; i < totalExe; i++)
        {
            if(eleitos[i].getFitness() == melhorFit)
            {
                acertos++;
                if(!melhores.contains(eleitos[i]))
                    melhores.add(eleitos[i]);
            }
        }        
        acertos = (acertos / totalExe) * 100; // Em %
        acertos = arredonda(acertos, 2); // Arredonda o valor dos acertos
        // Desvio padrao de fitness e tempo
        for(i = 0; i < totalExe; i++)
        {
            desvioF += Math.pow(mediaF - eleitos[i].getFitness(), 2);
            //desvioT += Math.pow(mediaT - tempoExecusao[i], 2);
        }
        desvioF = desvioF / (totalExe - 1);
        desvioF = Math.sqrt(desvioF);
        desvioF = arredonda(desvioF, 5); // Arredonda o valor do desvio
        mediaF = arredonda(mediaF, 4); // Arredonda o valor da media
        //desvioT = desvioT / (MAX_EXECUCOES - 1);        
        //desvioT = Math.sqrt(desvioT);       
        
        // Impressão dos resultados estatisticos em arquivo e na tela        
        try {
            FileWriter fw1 = new FileWriter("EstatisticasAleatorio.txt", true);
            FileWriter fw2 = new FileWriter("EstatisticasExecucoesAleatorio.txt", true);
            BufferedWriter bw1 = new BufferedWriter(fw1);
            BufferedWriter bw2 = new BufferedWriter(fw2);
            // Escreve: MEDIA fitness <TAB> DESVIO padrao fitness <TAB> ACERTOS (%) <TAB> Melhor fitness <TAB> TEMPO medio (ms)
            bw1.append(mediaF + "\t" + desvioF + "\t" + acertos + "%" + "\t" + melhorFit + "\t" + arredonda(mediaT, 2) + " ms");
            bw1.newLine();
            // Escreve de cada execução: Melhor fitness, tempo e as respostas 
            bw2.append("fitness <- c(" + arredonda(eleitos[0].getFitness(), 4));
            for(i = 1; i < totalExe; i++)            
                bw2.append(", " + arredonda(eleitos[i].getFitness(), 4));
            bw2.append(");");            
            bw2.newLine();
            bw2.append("tempos <- c(" + tempoExecusao[0]);
            for(i = 1; i < totalExe; i++)            
                bw2.append(", " + tempoExecusao[i]);
            bw2.append(");");            
            bw2.newLine();    
            bw2.append("Respostas:");
            bw2.newLine();
            for(i = 0; i < totalExe; i++)
            {
                bw2.append(eleitos[i].provCod());
                bw2.newLine();
            }
            bw2.newLine();
            bw1.close();                        
            bw2.close();
        }
        catch(IOException ex)
        {
            System.out.println("Erro ao escrever no arquivo de estatisticas do Aleatorio: " + ex.getMessage());
        }
        System.out.println("\nMedia FITNESS: " + mediaF + "\nDesvio padrão FITNESS: " + desvioF);
        System.out.println("\nMedia TEMPO: " + arredonda(mediaT, 2) + " ms ou " + (arredonda(mediaT/1000.0, 4)) + " seg");
        //System.out.println("Desvio padrão TEMPO: " + arredonda(desvioT, 5) + " ms ou " + (arredonda(desvioT/1000.0, 5)) + " seg");
        System.out.println("Acertos = " + acertos + "%");
        System.out.println("Melhor(es) provedor(es) das " + totalExe + " execusoes:");
        for(i = 0; i < melhores.size(); i++)        
            melhores.get(i).imprimeProvCod();
        System.out.println();
    }
}
