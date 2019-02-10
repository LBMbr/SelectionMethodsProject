/*
 * UDESC - Universidade do Estado de Santa Catarina
 * Aluno: Lucas Borges de Moraes
 * Curso: Mestrado em Computação Aplicada - MCA 
 * Orientadores: Adriano Fiorese e Rafael Parpinelli
 */
package metaHeuristicas;

import aplicacao.AvaliaFitness;
import modelosMetaheuristicas.PopulacaoBin;
import modelosMetaheuristicas.IndividuoBin;
import java.util.Random;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import static modelos.Constantes.*;

/**
 * Implementa a Evolução Diferencial Binária (EDB / BDE).
 * 
 * Pseudocódigo do Algoritmo de Evolução Diferencial Binária (EDB).
 * PARÂMETROS: Tamanho da população, número de variáveis, condição de parada, probabilidade de perturbacao e mutação.
 * t = 0
 * p = População inicial aleatória (TAM_POP, NUM_VARS)
 * Avaliação de fitness(p)
 * melhor = getMelhor(p)
 * Enquanto !CONDIÇÃO_DE_PARADA faça
 *      Para todo individuo i faça
 *          k = Individuo aleatorio de 1 a TAM_POP e k != i
 *          d = Variável aleatória de 1 a NUM_VARS
 *          Individuo y = p[i]
 *          Para tada variável j faça
 *              se OCORRER_CROSSOVER ou d = j então
 *                  se OCORRER_MUTAÇÃO então
 *                      y[j] = BitFlip(p[j])
 *                  senão
 *                      y[j] = p[k]
 *                  fim_se
 *              fim_se
 *          fim_para
 *          Avaliação de fitness(y)
 *          p[i] = SELEÇÃO_GULOSA(y, p[i])
 *      fim_para
 *      melhor = getMelhor(p)
 *      t = t + 1
 * fim_enquanto
 * PRINT(melhor)
 */
public class EDB 
{  
    /** A quantidade de provedores na base de dados. */
    private int NUM_PROV;    
    /** O número identificador da requisiçao. */
    private int NUM_REQ;    
    /** A quantidade de PIs na base de dados. */
    private int NUM_PIS;
    /** O número identificador da base de dados utilizada. */
    private int NUM_DB;
    /** O número maximo de execucoes do EDB. */
    private int MAX_EXECUCOES;
    /** O número maximo de geracoes do EDB. */
    private int MAX_GERACOES;    
    /** O tamanho maximo da populacao do EDB. */
    private int TAM_POP;
    /** A probabilidade de ocorrencia de Perturbaçoes, entre 0 e 100. */
    private int PROB_PR;
    /** A probabilidade de ocorrencia de Mutaçao, entre 0 e 100. */      
    private int PROB_MUTACAO;  
    /** Flag definindo se havera output em arquivo dos dados das geracoes do EDB ou não (false). */
    private boolean RELATORIO;
    /** Gerador randomico da classe. */
    private static final Random gerador = new Random();
    /**
     * Implementa o algoritmo de busca de fitness pela Evolução Diferencial Binária.
     * @param NUM_PROV O numero total de provedores da base de dados.
     * @param NUM_REQ O numero identificador da requisiçao do cliente.
     * @param NUM_PIS O numero total de PIs na base de dados.
     * @param NUM_DB O numero identificador da base de dados utilizada.
     * @param MAX_EXECUCOES O numero maximo de execucoes do EDB.
     * @param MAX_GERACOES O numero maximo de geracoes do EDB.
     * @param TAM_POP O tamanho maximo da populacao do EDB.
     * @param PROB_PR probabilidade de ocorrencia de Perturbações, entre 0 e 100.
     * @param PROB_MUTACAO A probabilidade de ocorrencia de Mutaçao, entre 0 e 100.
     */
    public EDB(int NUM_PROV, int NUM_REQ, int NUM_PIS, int NUM_DB, int MAX_EXECUCOES, int MAX_GERACOES, int TAM_POP, int PROB_PR, int PROB_MUTACAO)
    {
        if(NUM_PROV <= 0 || NUM_REQ <= 0 || NUM_PIS <= 0 || NUM_DB <= 0 || MAX_EXECUCOES <= 0 ||
           MAX_GERACOES <= 0 || TAM_POP <= 0 || PROB_PR < 0 || PROB_MUTACAO < 0)
            throw new IllegalArgumentException("EDB: Todos os parametros devem ser maiores que zero!");
        this.NUM_PROV = NUM_PROV;
        this.NUM_REQ = NUM_REQ;
        this.NUM_PIS = NUM_PIS;
        this.NUM_DB = NUM_DB;
        this.MAX_EXECUCOES = MAX_EXECUCOES;
        this.MAX_GERACOES = MAX_GERACOES;
        this.TAM_POP = TAM_POP;
        this.PROB_PR = PROB_PR;
        this.PROB_MUTACAO = PROB_MUTACAO;
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
    /** @return O numero maximo de execucoes do EDB. */
    public int getMAX_EXECUCOES() 
    {
        return MAX_EXECUCOES;
    }
    /** @return O numero maximo de geracoes do AG. */
    public int getMAX_GERACOES() 
    {
        return MAX_GERACOES;
    }
    /** @return O tamanho maximo da populacao do AG. */
    public int getTAM_POP()
    {
        return TAM_POP;
    }
    /** @return A probabilidade de ocorrencia de Perturbaçoes. */
    public int getPROB_PR() 
    {
        return PROB_PR;
    }
    /** @return A probabilidade de ocorrencia de Mutaçao. */
    public int getPROB_MUTACAO() 
    {
        return PROB_MUTACAO;
    } 
    /** @return A flag indicando se ha output em arquivo dos dados das geracoes desse algoritmo não. */
    public boolean isRELATORIO()
    {
        return RELATORIO;
    }
            
    /**
     * Roda o Algoritmo da Evolução Diferencial Binaria de busca de fitness.
     * @param RELATORIO Flag definindo se havera output em arquivo dos dados das geracoes do EDB ou não (false).
     * @param iniciais Os individuos da populacao inicial. Envie nulo para a populacao inicial ser totalmente aleatoria.
     * @return O(s) melhor(es) individuo(s) encontrado(s) em todas as execucoes.
     */
    public ArrayList<IndividuoBin> rodaEDB(boolean RELATORIO, ArrayList<boolean[]> iniciais)
    {
        this.RELATORIO = RELATORIO;
        // Carrega os dados
        String ArqBD = "Dados/Base" + NUM_DB + "/DADOS" + NUM_PROV + "x" + NUM_PIS + ".txt";
        String ArqReq = "Requisicoes/Base" + NUM_DB + "/REQ" + NUM_REQ + "_" + NUM_PIS + ".txt";
        AvaliaFitness af = new AvaliaFitness(ArqBD, ArqReq);  
        //af.imprime();
        
        // Variaveis
        int i, j, k, d, geracao, execucao, NUM_VARS = af.getBase_dados().getNumProvedores();
        long tempoInicial, tempoFinal;
        boolean aleatorio = false;
        PopulacaoBin p; // População corrente
        IndividuoBin melhor, novo, atual, outro;
        //Random gerador = new Random();        
        IndividuoBin[] eleitos = new IndividuoBin[MAX_EXECUCOES]; // Melhores invididuos de cada execusão
        double[] tempoExecusao = new double[MAX_EXECUCOES]; // Tempo de execusão de cada execusão
                
        System.out.println("# # # # # Algoritmo da Evolução Diferencial Binária # # # # #\n");        
        //System.out.println("Espaço de busca: " + (Math.pow(2, NUM_VARS) - 1));
        System.out.println("NumProv: " + NUM_PROV + "\nNumReq: " + NUM_REQ + "\nNumPIs: " + NUM_PIS + "\nNumDB: " + NUM_DB);
        
        // Solucao inicial do metodo
        if(iniciais == null || iniciais.isEmpty())
            aleatorio = true;
        
        // Inicia as execuções
        execucao = 1;
        // LOOP DE EXECUÇÕES DO EDB
        while(execucao <= MAX_EXECUCOES)
        {
            // Inicio do EDB
            geracao = 0;
            novo = new IndividuoBin(NUM_VARS); // Pivô
            
            // Inicia o cronômetro
            tempoInicial = System.currentTimeMillis();
            
            // População inicial do metodo
            p = (aleatorio) ? (new PopulacaoBin(TAM_POP, NUM_VARS)) : // Totalmente aleatoria
                              (new PopulacaoBin(TAM_POP, NUM_VARS, iniciais)); // Com os individuos passados
            // Avaliação de fitness da população
            for(i = 0; i < TAM_POP; i++)                
                af.fitness(p.getIndPos(i));  
            
            // Armazena o melhor individuo atual
            melhor = p.getMelhorInd();            
            // Armazena dados de fitness da população
            if(RELATORIO) geraRelatorioEDB(p, geracao, execucao); 
            //System.out.println("Geração inicial:"); p.imprimePopulacao();
            
            // LOOP EVOLUTIVO DO EDB
            while(geracao < MAX_GERACOES) // Por número de gerações
            {  
                // Para todo individuo na população
                for(i = 0; i < TAM_POP; i++) 
                {
                    // Pega o individuo corrente
                    atual = p.getIndPos(i);
                    // Copia o individuo na posição i
                    novo.clone(atual);
                    //novo.imprimeInd();                    
                    do {
                        k = gerador.nextInt(TAM_POP); // Posição do individuo para crossover
                    } while(k == i);
                    outro = p.getIndPos(k); // O "outro" para crossover                    
                    // Posição da variável que sofrerá alteração obrigatoriamente
                    d = gerador.nextInt(NUM_VARS); 
                    
                    //System.out.println("[k: " + (k+1) + "  d: " + (d+1) + "]");
                    //System.out.println("Individuo k:");
                    //p.getIndPos(k).imprimeInd();
                    //System.out.println();
                    
                    // Para toda variável do individuo i
                    for(j = 0; j < NUM_VARS; j++)
                    {
                        // A variável j sofrerá perturbação ?
                        if( j == d || (gerador.nextInt(100) < PROB_PR) )
                        {
                            // A variável sofrerá perturbação. Qual tipo?
                            if(gerador.nextInt(100) < PROB_MUTACAO)
                            {   // Mutação: Bit flip                                
                                novo.setCodPos(j, !(atual.getCodPos(j)) );
                                //System.out.println("Individuo " + (i+1) + " mutou na variavel " + (j+1));                                
                            }
                            else
                            {   // CrossOver com o individuo na posição "k"                                
                                novo.setCodPos(j, (outro.getCodPos(j)) );
                                //System.out.println("Individuo " + (i+1) + " sofreu crossover na variavel " + (j+1));                                
                            }                          
                        }
                    }
                    // Avaliação de fitness para o novo indivíduo gerado                    
                    af.fitness(novo);
                    //System.out.println("Novo individuo:");
                    //novo.imprimeInd();
                    
                    // Seleção gulosa: o novo individuo gerado é melhor?
                    if(novo.getFitness() > atual.getFitness())
                        atual.clone(novo); // Sustitui o melhor                    
                    //p.imprimePop();
                } 
                // Armazena o melhor individuo da nova população p a cada nova geração                
                melhor = p.getMelhorInd();                
                // Nova geração
                geracao++;
                //System.out.println("Geração " + geracao);
                // Armazena dados de fitness da população
                if(RELATORIO) geraRelatorioEDB(p, geracao, execucao); 
            } // Fim do EDB            
            // Para o cronômetro
            tempoFinal = (System.currentTimeMillis() - tempoInicial);
            // Guarda o tempo
            tempoExecusao[execucao - 1] = tempoFinal;
            // Guarda o melhor individuo dessa execução
            eleitos[execucao - 1] = melhor;
            
            // Imprime o melhor individuo
            //System.out.println("\nExecução " + execucao + ": ");
            if(!melhor.isPenalizado())
            {
                //System.out.println("Melhor fitness: " + melhor.getFitness());
                //System.out.print("Codificação:  ");
                //melhor.imprimeCod();
                //melhor.imprimeProvCod();                
            }
            else
            {
                System.out.println("Não foi encontrado um individuo válido com " + MAX_GERACOES + " gerações");  
                //System.out.println("Melhor fitness: " + melhor.getFitness());
                //System.out.print("Codificação:  ");
                //melhor.imprimeCod();
                //melhor.imprimeProvCod();
            }             
            //System.out.println("\nTempo da execução " + execucao + ": " + tempoFinal + " ms ou " + (tempoFinal/1000.0) + " seg");
            // Próxima execucao do EDB
            execucao++;
        } // Fim das execuções do EDB
                
        // DEBUG
        /*for(int i = 0; i < MAX_EXECUCOES; i++)
            System.out.print(eleitos[i]);
        System.out.println();*/
        
        // # # # # Estatisticas pós execuções do EDB # # # # 
        return estatisticaFinais(eleitos, tempoExecusao);
    } // FIM 
    
    /**************************************************************************/
    /********************** ESTATISTICAS E RELATORIOS *************************/
    /**************************************************************************/
    /**
     * Gera as estatisticas finais pós execuções do EDB, como fitness e tempo medio,
     * desvio padrao de fitness e tempo e a porcentagem de acertos.
     * @param eleitos Vetor com todos os invididuos eleitos pelo EDB a cada execusao.
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
        desvioF = arredonda(desvioF, 5); // Arredonda o valor do desvio
        mediaF = arredonda(mediaF, 4); // Arredonda o valor da media
        //desvioT = desvioT / (totalExe - 1);        
        //desvioT = Math.sqrt(desvioT);       
        
        // Impressão dos resultados estatisticos em arquivo e na tela
        FileWriter fw;
        BufferedWriter bw;
        try {
            fw = new FileWriter("EstatisticasEDB.txt", true);
            bw = new BufferedWriter(fw);
            // No arquivo: "EstatisticasEDB.txt"
            // Escreve: (Num req)(Num prov)(Num pis) <TAB> MEDIA fitness <TAB> DESVIO PADRAO fitness <TAB> ACERTOS (%) <TAB> Melhor fitness <TAB> TEMPO medio (ms)
            bw.append("(Req " + NUM_REQ + ")(Provs " + NUM_PROV + ")(PIs " + NUM_PIS + ")\t" + 
                mediaF + "\t" + desvioF + "\t" + acertos + "%" + "\t" + melhorFit + "\t" + arredonda(mediaT, 2));
            bw.newLine();
            bw.close();
            ////////////////////////////////////////////////////////////////////
            fw = new FileWriter("EstatisticasExecucoesEDB.txt", true);            
            bw = new BufferedWriter(fw);            
            // No arquivo: "EstatisticasExecucoesEDB.txt"
            // Escreve: (Num req)(Num prov)(Num pis)
            bw.append("(Req " + NUM_REQ + ")(Provs " + NUM_PROV + ")(PIs " + NUM_PIS + ")");
            bw.newLine();
            // Escreve de cada execução: Melhor fitness, tempo e as respostas 
            bw.append("fitness <- c(" + arredonda(eleitos[0].getFitness(), 4));
            for(i = 1; i < totalExe; i++)            
                bw.append(", " + arredonda(eleitos[i].getFitness(), 4));
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
            System.out.println("Erro ao escrever no arquivo de estatisticas do EDB: " + ex.getMessage());
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
        if(RELATORIO)
        {
            System.out.println("Preparando dados finais para relatorio...");
            juntaDadosEDB(melhorFit);
            System.out.println("Feito!\n");
        }
        return melhores;
    }
    
    /************************ FUNÇÕES DE RELATÓRIOS ***************************/
    /**
     * Função que guarda dados fundamentais para construção dos gráficos de
     * convergência e diversidade do EDB em diferentes arquivos texto.
     * Dados obtidos da população: Fitness do melhor indivíduo, fitness médio da
     * população, variabiliadade genotípica normalizada.
     * @param p População cujos dados devem ser armazenados.
     * @param geracaoAtual Número da geração atual do EDB.
     * @param geracaoFinal O número correspondente a útima geração/iteração do EDB.
     * @param execucaoAtual Número da execução atual do EDB.
     */
    private void geraRelatorioEDB(PopulacaoBin p, int geracaoAtual, int execucaoAtual) 
    {  
        if(geracaoAtual < 0 || geracaoAtual > MAX_GERACOES || execucaoAtual < 0 || execucaoAtual > MAX_EXECUCOES)
            throw new IllegalArgumentException("geraRelatorioEDB: Dados de entrada invalidos!");
        
        FileWriter fw;
        BufferedWriter bw;
        boolean append = true; // Adiciona os dados ao fim do arquivo
        boolean ultimo = false; // Marca a ultima geracao, false = gerações intermediárias -> Esta no meio da execução do EDB 
        
        if(geracaoAtual == 0) // Primeira geração -> Inicio do EDB
            append = false; // Sobreescreve o texto do arquivo
        
        if(geracaoAtual == MAX_GERACOES)
            ultimo = true; // Última geração -> Fim do EDB
        
        try {            
            fw = new FileWriter("temps/FitMelhorEDB" + execucaoAtual + ".txt", append);
            bw = new BufferedWriter(fw);
            bw.append(arredonda(p.getMelhorInd().getFitness(), 4) + (ultimo ? "":","));            
            bw.close();
            
            fw = new FileWriter("temps/FitMedioEDB" + execucaoAtual + ".txt", append);
            bw = new BufferedWriter(fw);
            bw.append(arredonda(p.getFitnessMedio(), 4) + (ultimo ? "":","));            
            bw.close();
            
            fw = new FileWriter("temps/VariabilidadeEDB" + execucaoAtual + ".txt", append);
            bw = new BufferedWriter(fw);
            bw.append(arredonda(p.variabilidadeGenetica(), 4) + (ultimo ? "":","));            
            bw.close();                 
        }
        catch (IOException ex)
        {
            System.out.println("geraRelatorioEDB: Erro ao escrever no arquivo: " + ex.getMessage());                     
        }
    }    
    
    /**
     * Agrega todos os dados do EDB em um único arquivo e prapara-os como entrada no R.
     * Imprime ao fim do arquivo de dados os plots da média das execuções a serem
     * calculadas no R.
     * @param melhorFitness Fitness do melhor individuo de todas as execuções.
     */
    private void juntaDadosEDB(double melhorFitness)
    {        
        try {  
            int i;
            FileWriter fw = new FileWriter("logs/Base" + NUM_DB + "_DadosEDB" + NUM_PROV + "x" + NUM_REQ + ".txt", false);            
            BufferedWriter bw = new BufferedWriter(fw);
            FileReader fr;
            BufferedReader br;
            
            for(i = 1; i <= MAX_EXECUCOES; i++)
            {
                ////////////////////////////////////////////////////////////////////////////////
                fr = new FileReader("temps/FitMelhorEDB" + i + ".txt");
                br = new BufferedReader(fr);
                bw.append("fitMelhorEDB" + i + " <- c(" + br.readLine() + ");");
                bw.newLine();
                br.close(); 
                fr.close();
                ////////////////////////////////////////////////////////////////////////////////
                fr = new FileReader("temps/FitMedioEDB" + i + ".txt");
                br = new BufferedReader(fr);
                bw.append("fitMedioEDB" + i + " <- c(" + br.readLine() + ");");  
                bw.newLine();
                br.close();
                fr.close();
                ////////////////////////////////////////////////////////////////////////////////
                fr = new FileReader("temps/VariabilidadeEDB" + i + ".txt");
                br = new BufferedReader(fr); 
                bw.append("variabilidadeEDB" + i + " <- c(" + br.readLine() + ");");
                bw.newLine();
                bw.newLine();
                br.close();
                fr.close();
                ////////////////////////////////////////////////////////////////////////////////
            }
            // Pos processamentos finais
            bw.append("geracoesEDB <- c(seq(0, " + MAX_GERACOES + "));");
            bw.newLine();
            /////////////////////////////////////
            String fitMelhor = "fitMelhorEDB <- (";
            String fitMedio = "fitMedioEDB <- (";
            String variabilidade = "variabilidadeEDB <- (";
            for(i = 1; i < MAX_EXECUCOES; i++)
            {                
                fitMelhor += "fitMelhorEDB" + i + " + ";
                fitMedio += "fitMedioEDB" + i + " + ";
                variabilidade += "variabilidadeEDB" + i + " + ";
            }            
            fitMelhor += "fitMelhorEDB" + MAX_EXECUCOES + ")/" + MAX_EXECUCOES + ";";
            fitMedio += "fitMedioEDB" + MAX_EXECUCOES + ")/" + MAX_EXECUCOES + ";";
            variabilidade += "variabilidadeEDB" + MAX_EXECUCOES + ")/" + MAX_EXECUCOES + ";";            
            bw.append(fitMelhor);
            bw.newLine();
            bw.append(fitMedio);
            bw.newLine();
            bw.append(variabilidade);
            bw.newLine();
            bw.newLine();
            // Normalizacao dos valores no R
            //bw.append("maxEDB <- max(max(fitMelhorEDB), max(fitMedioEDB));");
            //bw.append("maxEDB <- " + arredonda(melhorFitness, 4) + ";");
            //bw.newLine();
            //bw.append("minEDB <- min(min(fitMelhorEDB), min(fitMedioEDB));");
            //bw.newLine();
            //bw.append("fitMelhorNormEDB <- ((1.0/(maxEDB - minEDB)) * fitMelhorEDB - (1.0/(maxEDB - minEDB)) * minEDB);");
            //bw.newLine();
            //bw.append("fitMedioNormEDB <- ((1.0/(maxEDB - minEDB)) * fitMedioEDB - (1.0/(maxEDB - minEDB)) * minEDB);");
            //bw.newLine();
            //bw.newLine();
            // Função plot no R
            // Carregar arquivo: source("..//Desktop//DadosEDB.txt");
            // plot(vetor de dados, type="<p/i/o>", col="<COR>", xlab="<nome eixo x>", xlim/ylim = c(.., ..)
            //   ylab="<nome eixo y>", main="<Título>", sub = "<Subtitulo>")
            // lines(dados, type = "p/i/o", col = "COR") -> Adiciona uma curva
            // https://docs.ufpr.br/~aanjos/CE231/web/apostila.html
            // http://www.w3ii.com/pt/r/r_line_graphs.html
            // VARS: geracoes, fitMelhor, fitMedio e variabilidade      
            // SALVAR imagem: pdf("nome.pdf"); ou jpeg("nome.jpg"); ou png("nome.jpg");
            //                  comando para o grafico
            //                dev.off();
            bw.append("pdf(\"../Desktop/BDE_Converg" + NUM_PROV + "_Req" + NUM_REQ + ".pdf\");");
            bw.newLine(); // fitMelhorNormEDB
            bw.append("plot(fitMelhorEDB ~ geracoesEDB, type='l', col='blue', "
                    + "xlab='Generations', ylab='Fitness', ylim=c(0.94,1.0), "
                    + "main='Convergence of the mean of " + MAX_EXECUCOES + " executions (BDE)');");            
            bw.newLine(); // fitMedioNormEDB
            bw.append("lines(fitMedioEDB, type = 'l', col = 'red');");  
            bw.newLine();
            bw.append("dev.off();");
            bw.newLine();
            bw.newLine();
            bw.append("pdf(\"../Desktop/BDE_Var" + NUM_PROV + "_Req" + NUM_REQ + ".pdf\");");
            bw.newLine();
            bw.append("plot(variabilidadeEDB ~ geracoesEDB, type='l', col='blue', xlab='Generations', "
                    + "ylab='Genotypic Variability', ylim=c(0,1), "
                    + " main='Genetic variability of the mean of " + MAX_EXECUCOES + " executions (BDE)');");
            bw.newLine(); 
            bw.append("dev.off();");
            bw.newLine();
            bw.close();
        }
        catch (IOException ex)
        {
            System.err.println("juntaDadosEDB: Erro com os arquivos: " + ex.getMessage());
        }
    }
}