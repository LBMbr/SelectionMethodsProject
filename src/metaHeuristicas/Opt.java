/*
 * UDESC - Universidade do Estado de Santa Catarina
 * Aluno: Lucas Borges de Moraes
 * Curso: Mestrado em Computação Aplicada - MCA 
 * Orientadores: Adriano Fiorese e Rafael Parpinelli
 */
package metaHeuristicas;

import aplicacao.AvaliaFitness;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import static modelos.Constantes.*;
import modelosMetaheuristicas.IndividuoBin;

/**
 * Implementa o Algoritmo de busca local determinisitica 3-opt.
 */
public class Opt 
{
    /** A quantidade de provedores na base de dados. */
    private int NUM_PROV;    
    /** O número identificador da requisiçao. */
    private int NUM_REQ;    
    /** A quantidade de PIs na base de dados. */
    private int NUM_PIS;
    /** O número identificador da base de dados utilizada. */
    private int NUM_DB;
    /** O número maximo de execucoes do 3-opt. */
    private int MAX_EXECUCOES;
    /** O número maximo de iteracoes do 3-opt. */
    private int IT_MAX;    
    
    /** Flag definindo se havera output em arquivo dos dados das iteracoes do SA ou não (false). */
    //private boolean RELATORIO;
    /** Gerador randomico da classe. */
    private static final Random gerador = new Random();
    
    /**
     * Implementa o algoritmo da busca local determinisitica 3-opt.
     * @param NUM_PROV Numero total de provedores da base de dados.
     * @param NUM_REQ Numero identificador da requisiçao do cliente.
     * @param NUM_PIS Numero total de PIs na base de dados.
     * @param NUM_DB Numero identificador da base de dados utilizada.
     * @param MAX_EXECUCOES Numero maximo de execucoes do 3-opt.
     * @param IT_MAX Numero maximo de iteracoes do 3-opt (avaliacoes de fitness). 
     */
    public Opt(int NUM_PROV, int NUM_REQ, int NUM_PIS, int NUM_DB, int MAX_EXECUCOES, int IT_MAX)
    {
        if(NUM_PROV <= 0 || NUM_REQ <= 0 || NUM_PIS <= 0 || NUM_DB <= 0 || MAX_EXECUCOES <= 0 || IT_MAX <= 0)
            throw new IllegalArgumentException("3-opt: Todos os parametros devem ser maiores que zero!");
        this.NUM_PROV = NUM_PROV;
        this.NUM_REQ = NUM_REQ;
        this.NUM_PIS = NUM_PIS;
        this.NUM_DB = NUM_DB;
        this.MAX_EXECUCOES = MAX_EXECUCOES;
        this.IT_MAX = IT_MAX;        
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
    /** @return A flag indicando se ha output em arquivo dos dados das geracoes desse algoritmo não. */
    /*public boolean isRELATORIO()
    {
        return RELATORIO;
    }*/
    
    /**
     * Roda o Algoritmo Genetico de busca de fitness.     
     * @param s0 A solucao candidata inicial. Envie nulo para a solucao inicial ser aleatoria.
     * @return A melhor solucao encontrada pelo SA, a partir da solucao inicial, s0.
     */
    public ArrayList<IndividuoBin> rodaOpt(boolean[] s0)
    {  
        //this.RELATORIO = RELATORIO;
        // Carrega os dados
        String ArqBD = "Dados/Base" + NUM_DB + "/DADOS" + NUM_PROV + "x" + NUM_PIS + ".txt";
        String ArqReq = "Requisicoes/Base" + NUM_DB + "/REQ" + NUM_REQ + "_" + NUM_PIS + ".txt";
        AvaliaFitness af = new AvaliaFitness(ArqBD, ArqReq);
        //af.imprime();        
        //af.imprimeTabAtend();
        
        // Variaveis
        int i, j, it, flag, maior, execucao, numProvs = af.getBase_dados().getNumProvedores();
        int[] pos = new int[3]; // Vetor de n posicoes aleatorias do n-opt (ex: 3)
        long tempoInicial, tempoFinal;
        double maiorfitness;
        boolean aleatorio, sequencial;
        boolean[] atualCod, aux = new boolean[numProvs];
        IndividuoBin atual;        
        IndividuoBin[] eleitos = new IndividuoBin[MAX_EXECUCOES]; // Melhores invididuos de cada execusão
        double[] tempoExecusao = new double[MAX_EXECUCOES]; // Tempo de execusão de cada execusão
        final boolean[][] opcoes = {{false, false, false},  // 0 0 0  // 3-opt tem 2^3 opcoes
                                    {false, false, true},   // 0 0 1
                                    {false, true,  false},  // 0 1 0
                                    {false, true,  true},   // 0 1 1
                                    {true,  false, false},  // 1 0 0
                                    {true,  false, true},   // 1 0 1
                                    {true,  true,  false},  // 1 1 0
                                    {true,  true,  true}};  // 1 1 1
        IndividuoBin[] candidatos = new IndividuoBin[opcoes.length]; // Size: 2^n, n-opt, ex: n = 3 (3-opt) 
        for(i = 0; i < candidatos.length; i++)
            candidatos[i] = new IndividuoBin(numProvs); // Aloca memoria
        
        // DEBUG
        /*for(i = 0; i < opcoes.length; i++) {
            for(j = 0; j < opcoes[i].length; j++)
                System.out.print((opcoes[i][j] ? 1 : 0) + " ");
            System.out.println();
        }*/
        
        // Solucao inicial do metodo
        aleatorio = (s0 == null || s0.length != numProvs);
                
        System.out.println("# # # # # Algoritmo 3-opt # # # # #\n");        
        //System.out.println("Espaço de busca: " + (Math.pow(2, numProvs) - 1));
        System.out.println("NumProv: " + NUM_PROV + "\nNumReq: " + NUM_REQ + "\nNumPIs: " + NUM_PIS + "\nNumDB: " + NUM_DB);
                
        // DEBUG
        /*System.out.println("\nO numero total de execucoes: " + MAX_EXECUCOES);
        System.out.println("O número maximo de iteracoes: " + IT_MAX);
        System.out.println("A solucao inicial (s0): ");  imprimeVet(s0);
        System.out.println();*/
        
        // Inicia as execuções
        execucao = 1;        
        // LOOP DE EXECUÇÕES DO 3-OPT
        while(execucao <= MAX_EXECUCOES)
        {   // Inicio do 3-opt
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
            // Iniciando a solucao inicial
            it = 1; // Iteracoes totais (Avaliacoes de fitness)            
            atual = new IndividuoBin(s0);
            af.fitness(atual);
            sequencial = true;
            atualCod = atual.getCod();
            // DEBUG
            //System.out.println("\n-------------------------------");
            //System.out.println("A solucao inicial:");
            //System.out.println("-------------------------------");
            //atual.imprimeCod();
            //System.out.println("Fitness: " + atual.getFitness());
            //System.out.println("-------------------------------\n");
            // Enquanto nao atingir o numero total de iteracoes
            while(it < IT_MAX)
            {
                // Escolha sequencial de posicoes
                if(sequencial)
                {
                    // Gera o inicio da posicao sequencial
                    pos[0] = gerador.nextInt(numProvs - 2); // numCPs - (n - 1), n-opt (n = 3)
                    //System.out.println("Iteracao " + it + ")\nPosicao sorteada (seq): " + pos[0] + "\n");                    
                    // Para cada diferente opcao disponivel
                    for(i = 0; i < opcoes.length; i++)
                    {
                        flag = 0;
                        // Intervalo alvo, testa a opcao de linha "i"
                        for(j = pos[0]; j < (pos[0] + 3); j++)
                        {   
                            //System.out.print("opc " + i + ": aux[" + j + "] = ");
                            if(atualCod[j] != opcoes[i][j - pos[0]])
                            {
                                aux[j] = opcoes[i][j - pos[0]];
                                //System.out.println(aux[j] + " (opcoes[" + i + "][" + (j - pos[0]) + "] = " + opcoes[i][j - pos[0]] + ")");
                            }
                            else
                            {
                                aux[j] = atualCod[j];
                                flag++; // Para quando já for iqual a codificacao corrente de coluna "j"
                                //System.out.println(aux[j] + " (opcoes[" + i + "][" + (j - pos[0]) + "] = atualCod[" + j + "] = " + atualCod[j] + ")");
                            }
                        }
                        // DEBUG
                        //System.out.println("\nSolucao atual:");  atual.imprimeCod();
                        //System.out.println("Fitness: " + atual.getFitness());
                        //System.out.println("Candidato " + (i+1) + ":");
                        if(flag != 3)
                        {   // Diferente da codificacao corrente                            
                            // Intervalo antes do ponto sorteado
                            for(j = 0; j < pos[0]; j++)
                                aux[j] = atualCod[j];  // Repete a codificacao
                            // Intervalo depois do ponto sorteado
                            for(j = (pos[0] + 3); j < numProvs; j++)
                                aux[j] = atualCod[j];  // Repete a codificacao
                            
                            candidatos[i].setCodificacao(aux);
                            af.fitness(candidatos[i]);
                            it++;
                            // DEBUG                            
                            //candidatos[i].imprimeCod();
                            //System.out.println("fitness: " + candidatos[i].getFitness());
                            //System.out.println("Nova iteracao (" + it + ")\n");
                        }
                        else
                        {   // A opcao testada eh a mesma do candidato atual/corrente
                            candidatos[i].clone(atual);
                            //System.out.println("Repete o candidato atual!\n");
                        }
                    }
                }
                else // Escolha aleatoria de posicoes
                {
                    // Gera as posicoes aleatorias sem repetir
                    pos[0] = gerador.nextInt(numProvs);
                    do {
                        pos[1] = gerador.nextInt(numProvs);
                    } while(pos[1] == pos[0]);
                    do {
                        pos[2] = gerador.nextInt(numProvs);
                    } while(pos[2] == pos[0] || pos[2] == pos[1]);
                    // DEBUG
                    //System.out.println("Iteracao " + it + ")");
                    //for(j = 0; j < pos.length; j++)
                    //    System.out.println("Posicao sorteada " + (j + 1) + ": " + pos[j]);
                    //System.out.println();
                    // Para cada diferente opcao disponivel
                    for(i = 0; i < opcoes.length; i++)
                    {
                        // Repete a codificacao anterior
                        for(j = 0; j < numProvs; j++)
                            aux[j] = atualCod[j];
                        
                        flag = 0;
                        // Altera so as posicoes sorteadas
                        for(j = 0; j < pos.length; j++)
                        {
                            //System.out.print("opc " + i + ": aux[" + pos[j] + "] = ");
                            if(aux[pos[j]] != opcoes[i][j])
                            {
                                aux[pos[j]] = opcoes[i][j];
                                //System.out.println(aux[pos[j]] + " (opcoes[" + i + "][" + j + "] = " + opcoes[i][j] + ")");
                            }
                            else
                            {
                                flag++;
                                //System.out.println(aux[pos[j]] + " (opcoes[" + i + "][" + j + "] = atualCod[" + pos[j] + "] = " + atualCod[pos[j]] + ")");
                            }
                        }
                        // DEBUG
                        //System.out.println("\nSolucao atual:");  atual.imprimeCod();
                        //System.out.println("Fitness: " + atual.getFitness());
                        //System.out.println("Candidato " + (i+1) + ":");
                        if(flag != 3)
                        {   // Diferente da codificacao corrente
                            candidatos[i].setCodificacao(aux);
                            af.fitness(candidatos[i]);
                            it++;
                            // DEBUG                            
                            //candidatos[i].imprimeCod();
                            //System.out.println("fitness: " + candidatos[i].getFitness());
                            //System.out.println("Nova iteracao (" + it + ")\n");
                        }
                        else
                        {
                            candidatos[i].clone(atual);
                            //System.out.println("Repete o candidato atual!\n");
                        }
                    }
                }                
                // Acha o candidato de maior fitness (selecao gulosa)
                maior = -1;
                maiorfitness = -1.0;                
                for(i = 0; i < candidatos.length; i++)
                {
                    // DEBUG
                    //System.out.println("Candidato " + (i+1));
                    //candidatos[i].imprimeCod();
                    //System.out.println("Fitness " + candidatos[i].getFitness() + "\n");
                    if(candidatos[i].getFitness() > maiorfitness)
                    {
                        maiorfitness = candidatos[i].getFitness();
                        maior = i;
                    }
                }
                // Atualiza o individuo corrente (guloso)
                atual.clone(candidatos[maior]);
                atualCod = atual.getCod();
                // DEBUG
                //System.out.println("-------------------------------");
                //System.out.println("Novo candidato atual (" + it + "): ");
                //System.out.println("-------------------------------");
                //atual.imprimeInd();
                //System.out.println("-------------------------------\n");
                // Intercalonando as sequenciais e aleatorias
                sequencial = ! sequencial;
            } // Fim 3-opt
            // Pára o cronômetro
            tempoFinal = (System.currentTimeMillis() - tempoInicial);
            // DEBUG
            //System.out.print("\nCandidato final: " + atual);
                        
            // Guarda o melhor individuo e o tempo dessa execução
            eleitos[execucao - 1] = atual;            
            tempoExecusao[execucao - 1] = tempoFinal; 
            // DEBUG
            //System.out.println("\nExecução " + execucao + ": ");            
            //System.out.print("Melhor solucao: ");
            //atual.imprimeInd();
            //System.out.println("Melhor energia: " + Em);
            //System.out.println("Tempo da execução " + execucao + ": " + tempoFinal + " ms ou " + (tempoFinal/1000.0) + " seg");
            
            // Próxima execucao do 3-Opt
            execucao++;
        } // Fim das execucoes
        // DEBUG
        //for(int i = 0; i < MAX_EXECUCOES; i++) System.out.print(eleitos[i]); System.out.println();
        
        // # # # # Estatisticas pós execuções do 3-opt # # # #        
        return estatisticaFinais(eleitos, tempoExecusao);
    } // FIM
    
    /**************************************************************************/
    /********************** ESTATISTICAS E RELATORIOS *************************/
    /**************************************************************************/
    /**
     * Gera as estatisticas finais pós execuções do opt, como fitness e tempo medio,
     * desvio padrao de fitness e tempo e a porcentagem de acertos.
     * @param eleitos Vetor com todos os invididuos eleitos pelo metodo a cada execusao.
     * @param tempoExecusao Vetor com todos os tempos de execusao de cada execusao.
     * @return O(s) melhor(es) individuo(s) encontrado(s) em todas as execucoes.
     */
    private ArrayList<IndividuoBin> estatisticaFinais(IndividuoBin[] eleitos, double[] tempoExecusao)
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
        desvioF = arredonda(desvioF, 10); // Arredonda o valor do desvio
        mediaF = arredonda(mediaF, 10); // Arredonda o valor da media
        //desvioT = desvioT / (totalExe - 1);        
        //desvioT = Math.sqrt(desvioT);       
        
        // Impressão dos resultados estatisticos em arquivo e na tela
        FileWriter fw;
        BufferedWriter bw;            
        try {
            fw = new FileWriter("EstatisticasOpt.txt", true);
            bw = new BufferedWriter(fw);
            // No arquivo: "EstatisticasOpt.txt"
            // Escreve: (Num req)(Num prov)(Num pis) <TAB> MEDIA fitness <TAB> DESVIO PADRAO fitness <TAB> ACERTOS (%) <TAB> Melhor fitness <TAB> TEMPO medio (ms)
            bw.append("(Req " + NUM_REQ + ")(Provs " + NUM_PROV + ")(PIs " + NUM_PIS + ")\t" +
                mediaF + "\t" + desvioF + "\t" + acertos + "%" + "\t" + melhorFit + "\t" + arredonda(mediaT, 2));
            bw.newLine();
            bw.close();
            ////////////////////////////////////////////////////////////////////
            fw = new FileWriter("EstatisticasExecucoesOpt.txt", true);            
            bw = new BufferedWriter(fw);            
            // No arquivo: "EstatisticasExecucoesAG.txt"
            // Escreve: (Num req)(Num prov)(Num pis)
            bw.append("(Req " + NUM_REQ + ")(Provs " + NUM_PROV + ")(PIs " + NUM_PIS + ")");
            bw.newLine();
            // Escreve de cada execução: Melhor fitness, tempo e as respostas 
            bw.append("fitness <- c(" + arredonda(eleitos[0].getFitness(), 10));
            for(i = 1; i < totalExe; i++)            
                bw.append(", " + arredonda(eleitos[i].getFitness(), 10));
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
                bw.append(eleitos[i].provCod());
                bw.newLine();
            }
            bw.newLine();                                   
            bw.close();
        }
        catch(IOException ex)
        {
            System.out.println("Erro ao escrever no arquivo de estatisticas do Opt: " + ex.getMessage());
        }
        System.out.println("\nMedia FITNESS: " + mediaF + "\nDesvio padrão FITNESS: " + desvioF);
        System.out.println("\nMedia TEMPO: " + arredonda(mediaT, 2) + " ms ou " + (arredonda(mediaT/1000.0, 4)) + " seg");
        //System.out.println("Desvio padrão TEMPO: " + arredonda(desvioT, 5) + " ms ou " + (arredonda(desvioT/1000.0, 5)) + " seg");
        System.out.println("Acertos = " + acertos + "%");
        /*System.out.println("Melhor(es) provedor(es) das " + totalExe + " execusoes:");
        for(i = 0; i < melhores.size(); i++)        
            melhores.get(i).imprimeProvCod();*/
        System.out.println();
        
        // Junta todos os dados armazenados em um unico arquivo de relatorio
        /*if(RELATORIO)
        {
            System.out.println("Preparando dados finais para relatorio...");
            juntaDadosAG(melhorFit);
            System.out.println("Feito!\n");
        }*/
        return melhores;
    }
}
