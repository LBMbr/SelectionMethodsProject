/*
 * UDESC - Universidade do Estado de Santa Catarina
 * Aluno: Lucas Borges de Moraes
 * Curso: Mestrado em Computação Aplicada - MCA 
 * Orientadores: Adriano Fiorese e Rafael Parpinelli
 */
package metaHeuristicas;

import modelosMetaheuristicas.PopulacaoBin;
import modelosMetaheuristicas.IndividuoBin;
import aplicacao.AvaliaFitness;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import static modelos.Constantes.*;
import static modelosMetaheuristicas.OperadorGenetico.*;
import static modelosMetaheuristicas.OperadorSelecao.*;

/**
 * Implementa o Algoritmo Genético (AG / GA).
 * 
 * Pseudocódigo do Algoritmo Genético (AG) básico.
 * PARÂMETROS: Tamanho da população, número de variáveis, condição de parada, probabilidade de crossover e mutação.
 * t = 0
 * p0 = População inicial aleatória (TAM_POP, NUM_VARS)
 * Avaliação de fitness (p0)
 * melhor = getMelhor(p)
 * Enquanto !CONDIÇÃO_DE_PARADA faça
 *      p_prox = SELEÇÃO_FITNESS(p0)
 *      CROSSOVER(p_prox, PROB_CROSSOVER)
 *      MUTAÇÃO(p_prox, PROB_MUTAÇÃO)
 *      Avaliação de fitness(p_prox)
 *      ELETISMO(p_prox, MELHOR_p0)
 *      p0 = p_prox
 *      melhor = getMelhor(p0)
 *      t = t + 1
 * fim_enquanto
 * PRINT(melhor)
 */
public class AG 
{ 
    /** A quantidade de provedores na base de dados. */
    private int NUM_PROV;    
    /** O número identificador da requisiçao. */
    private int NUM_REQ;    
    /** A quantidade de PIs na base de dados. */
    private int NUM_PIS;
    /** O número identificador da base de dados utilizada. */
    private int NUM_DB;
    /** O número maximo de execucoes do AG. */
    private int MAX_EXECUCOES;
    /** O número maximo de geracoes do AG. */
    private int MAX_GERACOES;    
    /** O tamanho maximo da populacao do AG. */
    private int TAM_POP;
    /** A probabilidade de ocorrencia de CrossOver, entre 0 e 100. */
    private int PROB_CROSSOVER;
    /** A probabilidade de ocorrencia de Mutaçao, entre 0 e 100. */      
    private int PROB_MUTACAO;
    /** Flag definindo se havera output em arquivo dos dados das geracoes do AG ou não (false). */
    private boolean RELATORIO;
   /** Flag definindo se a seleção será por torneio (true) ou roleta (false). */
    //private static final boolean TORNEIO = true; // Sempre uso torneio
    /**
     * Implementa o algoritmo genetico de busca de fitness.
     * @param NUM_PROV O numero total de provedores da base de dados.
     * @param NUM_REQ O numero identificador da requisiçao do cliente.
     * @param NUM_PIS O numero total de PIs na base de dados.
     * @param NUM_DB O numero identificador da base de dados utilizada.
     * @param MAX_EXECUCOES O numero maximo de execucoes do AG.
     * @param MAX_GERACOES O numero maximo de geracoes do AG.
     * @param TAM_POP O tamanho maximo da populacao do AG.
     * @param PROB_CROSSOVER A probabilidade de ocorrencia de CrossOver, entre 0 e 100.
     * @param PROB_MUTACAO A probabilidade de ocorrencia de Mutaçao, entre 0 e 100.
     */
    public AG(int NUM_PROV, int NUM_REQ, int NUM_PIS, int NUM_DB, int MAX_EXECUCOES, int MAX_GERACOES, int TAM_POP, int PROB_CROSSOVER, int PROB_MUTACAO)
    {
        if(NUM_PROV <= 0 || NUM_REQ <= 0 || NUM_PIS <= 0 || NUM_DB <= 0 || MAX_EXECUCOES <= 0 ||
           MAX_GERACOES <= 0 || TAM_POP <= 0 || PROB_CROSSOVER < 0 || PROB_MUTACAO < 0)
            throw new IllegalArgumentException("AG: Todos os parametros devem ser maiores que zero!");
        this.NUM_PROV = NUM_PROV;
        this.NUM_REQ = NUM_REQ;
        this.NUM_PIS = NUM_PIS;
        this.NUM_DB = NUM_DB;
        this.MAX_EXECUCOES = MAX_EXECUCOES;
        this.MAX_GERACOES = MAX_GERACOES;
        this.TAM_POP = TAM_POP;
        this.PROB_CROSSOVER = PROB_CROSSOVER;
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
    /** @return O numero maximo de execucoes do AG. */
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
    /** @return A probabilidade de ocorrencia de CrossOver. */
    public int getPROB_CROSSOVER() 
    {
        return PROB_CROSSOVER;
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
     * Roda o Algoritmo Genetico de busca de fitness.
     * @param RELATORIO Flag definindo se havera output em arquivo dos dados das geracoes do AG ou não (false).
     * @param iniciais Os individuos da populacao inicial. Envie nulo para a populacao inicial ser totalmente aleatoria.
     * @return O(s) melhor(es) individuo(s) encontrado(s) em todas as execucoes.
     */
    public ArrayList<IndividuoBin> rodaAG(boolean RELATORIO, ArrayList<boolean[]> iniciais)
    {  
        this.RELATORIO = RELATORIO;
        // Carrega os dados
        String ArqBD = "Dados/Base" + NUM_DB + "/DADOS" + NUM_PROV + "x" + NUM_PIS + ".txt";
        String ArqReq = "Requisicoes/Base" + NUM_DB + "/REQ" + NUM_REQ + "_" + NUM_PIS + ".txt";
        AvaliaFitness af = new AvaliaFitness(ArqBD, ArqReq);
        //af.imprime();        
        
        // Variaveis
        int i, NUM_VARS = af.getBase_dados().getNumProvedores();         
        int geracao, execucao, TAM_TORNEIO = 5; // Para seleção com torneio 
        //double C = 1.20; // Para escalonamento linear com roleta, com dominio: 1.20 <= C <= 2.00
        long tempoInicial, tempoFinal;
        boolean aleatorio = false;
        PopulacaoBin p0; // População atual
        PopulacaoBin prox; // Proxima população
        IndividuoBin melhor; // Melhor individuo de p0
        double MNDF = 0.00; // Para calculo da variabilidade genética        
        IndividuoBin[] eleitos = new IndividuoBin[MAX_EXECUCOES]; // Melhores invididuos de cada execusão
        double[] tempoExecusao = new double[MAX_EXECUCOES]; // Tempo de execusão de cada execusão
        
        System.out.println("# # # # # Algoritmo Genético de Busca # # # # #\n");        
        //System.out.println("Espaço de busca: " + (Math.pow(2, NUM_VARS) - 1));
        System.out.println("NumProv: " + NUM_PROV + "\nNumReq: " + NUM_REQ + "\nNumPIs: " + NUM_PIS + "\nNumDB: " + NUM_DB);
        
        // Solucao inicial do metodo
        if(iniciais == null || iniciais.isEmpty())
            aleatorio = true;
        
        // Inicia as execuções
        execucao = 1;
        // LOOP DE EXECUÇÕES DO AG
        while(execucao <= MAX_EXECUCOES)
        {
            // Inicio do AG
            geracao = 0;
            // Inicia o cronômetro
            tempoInicial = System.currentTimeMillis();
            
            // População inicial do metodo
            p0 = (aleatorio) ? (new PopulacaoBin(TAM_POP, NUM_VARS)) : // Totalmente aleatoria
                               (new PopulacaoBin(TAM_POP, NUM_VARS, iniciais)); // Com os individuos passados
            // Avaliação de fitness da população
            for(i = 0; i < TAM_POP; i++)
                af.fitness(p0.getIndPos(i)); 
            
            // Armazena o melhor individuo atual
            melhor = p0.getMelhorInd();
            if(RELATORIO) 
            {   // Armazena dados de fitness da população
                geraRelatorioAG(p0, geracao, execucao);
                // Armazena o MNDF da população atual
                MNDF = p0.getMNDF();
            }
            //System.out.println("Geração inicial:"); p0.imprimePopulacao();
            
            // LOOP EVOLUTIVO DO AG
            while(geracao < MAX_GERACOES) // Por número de gerações
            {
                // # # # # # # # # # # SELEÇÃO # # # # # # # # # #                
                //if(TORNEIO)
                //{
                prox = torneio(p0, TAM_TORNEIO);
                /*}
                else
                {   // Roleta com escalonamento linear
                C += arredonda(0.80/(double)MAX_GERACOES, 5);
                //System.out.println("Geracao: " + geracao + ", C = " + arredonda(C, 4));
                EscalonamentoLinear.escalonamento(p0, C);
                //C += arredonda(0.80/(double)MAX_GERACOES, 5);
                prox = roleta(p0);
                }*/
                //System.out.println("População selecionada:"); prox.imprimePopulacao(); 
                // # # # # # # OPERADORES GENÉTICOS # # # # # #
                // CROSSOVER
                //for(i = 0; i < TAM_POP; i = i + 2)            
                //    onePointCrossOver(prox.getIndPos(i), prox.getIndPos(i + 1), PROB_CROSSOVER);
                //prox.imprimePopulacao();
                // MUTAÇÃO
                //for(i = 0; i < TAM_POP; i++)            
                //    bitFlipMutation(prox.getIndPos(i), PROB_MUTACAO);
                // CROSSOVER e MUTAÇÃO
                for(i = 0; i < TAM_POP; i = i + 2) 
                {
                    onePointCrossOver(prox.getIndPos(i), prox.getIndPos(i + 1), PROB_CROSSOVER);
                    bitFlipMutation(prox.getIndPos(i), PROB_MUTACAO);
                    bitFlipMutation(prox.getIndPos(i + 1), PROB_MUTACAO);
                }
                //System.out.println("População nova gerada:");  prox.imprimePopulacao(); 
                // # # # # # # FIM OPERADORES GENÉTICOS # # # # # #

                // Substituição da população antiga (p0) pela nova gerada (prox)
                p0.clone(prox);
                // Recupera o valor de MNDF atual, perdido pelo comando acima
                if(RELATORIO) p0.setMNDF(MNDF);                
                // Avaliação de fitness da população nova
                for(i = 0; i < TAM_POP; i++)
                    af.fitness(p0.getIndPos(i));                               
                // ELETISMO - Substitui o melhor individuo
                p0.eletismo(melhor); 
                // Armazena o melhor individuo de cada nova geração
                melhor = p0.getMelhorInd();
                // Nova geração
                geracao++;
                //System.out.println("Geração " + geracao);
                // Ajuste de escala dos denominadores do fitness
                //if((geracao % 500 == 0) && (geracao != MAX_GERACOES)) af.atualizaMaximos(p0);
                if(RELATORIO) 
                {   // Armazena dados de fitness da população e variabilidade genetica
                    geraRelatorioAG(p0, geracao, execucao);
                    // Atualiza o valor de MNDF calculado na função acima
                    MNDF = p0.getMNDF();
                }                
            } // Fim do AG
            // Pára o cronômetro
            tempoFinal = (System.currentTimeMillis() - tempoInicial);
            // Guarda o tempo
            tempoExecusao[execucao - 1] = tempoFinal; 
            // Guarda o melhor individuo dessa execução
            eleitos[execucao - 1] = melhor;
            
            // Reinicia os valores padroes dos denominadores de didisoes para a próxima execução
            //af.reiniciaMaximosPadroes();
            // Reinicia o C para a próxima execução
            //if(!TORNEIO)  C = 1.2;            
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
            // Próxima execucao do AG
            execucao++;
        } // Fim das execuções do AG
        
        // DEBUG
        /*for(int i = 0; i < MAX_EXECUCOES; i++)
        System.out.print(eleitos[i]);
        System.out.println();*/
        // # # # # Estatisticas pós execuções do AG # # # #        
        return estatisticaFinais(eleitos, tempoExecusao);
    } // FIM
    
    /**************************************************************************/
    /********************** ESTATISTICAS E RELATORIOS *************************/
    /**************************************************************************/
    /**
     * Gera as estatisticas finais pós execuções do AG, como fitness e tempo medio,
     * desvio padrao de fitness e tempo e a porcentagem de acertos.
     * @param eleitos Vetor com todos os invididuos eleitos pelo AG a cada execusao.
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
            fw = new FileWriter("EstatisticasAG.txt", true);
            bw = new BufferedWriter(fw);
            // No arquivo: "EstatisticasAG.txt"
            // Escreve: (Num req)(Num prov)(Num pis) <TAB> MEDIA fitness <TAB> DESVIO PADRAO fitness <TAB> ACERTOS (%) <TAB> Melhor fitness <TAB> TEMPO medio (ms)
            bw.append("(Req " + NUM_REQ + ")(Provs " + NUM_PROV + ")(PIs " + NUM_PIS + ")\t" + 
                mediaF + "\t" + desvioF + "\t" + acertos + "%" + "\t" + melhorFit + "\t" + arredonda(mediaT, 2));
            bw.newLine();
            bw.close();
            ////////////////////////////////////////////////////////////////////
            fw = new FileWriter("EstatisticasExecucoesAG.txt", true);            
            bw = new BufferedWriter(fw);            
            // No arquivo: "EstatisticasExecucoesAG.txt"
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
            System.out.println("Erro ao escrever no arquivo de estatisticas do AG: " + ex.getMessage());
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
            juntaDadosAG(melhorFit);
            System.out.println("Feito!\n");
        }
        return melhores;
    }
    
    /************************ FUNÇÕES DE RELATÓRIOS ***************************/
    /**
     * Função que guarda dados fundamentais para construção dos gráficos de
     * convergência e diversidade do AG em diferentes arquivos texto.
     * Dados obtidos da população: Fitness do melhor indivíduo, fitness médio da
     * população, variabiliadade genotípica normalizada, coeficiente MNDF atual da população.
     * @param p População cujos dados devem ser armazenados.
     * @param geracaoAtual Número da geração atual do AG.
     * @param execucaoAtual Número da execução atual do AG.
     */
    private void geraRelatorioAG(PopulacaoBin p, int geracaoAtual, int execucaoAtual) 
    {  
        if(geracaoAtual < 0 || geracaoAtual > MAX_GERACOES || execucaoAtual < 0 || execucaoAtual > MAX_EXECUCOES)
            throw new IllegalArgumentException("geraRelatorioAG: Dados de entrada invalidos!");
        
        FileWriter fw;
        BufferedWriter bw;
        boolean append = true; // Adiciona os dados ao fim do arquivo
        boolean ultimo = false; // Marca a ultima geracao, false = gerações intermediárias -> Esta no meio da execução do AG 
        
        if(geracaoAtual == 0) // Primeira geração -> Inicio do AG
            append = false; // Sobreescreve o texto do arquivo
        
        if(geracaoAtual == MAX_GERACOES)
            ultimo = true; // Última geração -> Fim do AG 
                 
        try {
            fw = new FileWriter("temps/FitMelhorAG" + execucaoAtual + ".txt", append);
            bw = new BufferedWriter(fw);
            bw.append(arredonda(p.getMelhorInd().getFitness(), 4) + (ultimo ? "":","));            
            bw.close();
            
            fw = new FileWriter("temps/FitMedioAG" + execucaoAtual + ".txt", append);
            bw = new BufferedWriter(fw);
            bw.append(arredonda(p.getFitnessMedio(), 4) + (ultimo ? "":","));            
            bw.close();
            
            fw = new FileWriter("temps/VariabilidadeAG" + execucaoAtual + ".txt", append);
            bw = new BufferedWriter(fw);
            bw.append(arredonda(p.variabilidadeGenetica(), 4) + (ultimo ? "":","));            
            bw.close();
            
            /*fw = new FileWriter("temps/MNDF_AG" + execucaoAtual + ".txt", append);
            bw = new BufferedWriter(fw);
            bw.append(arredonda(p.getMNDF(), 4) + (ultimo ? "":","));            
            bw.close();*/
        }
        catch(IOException ex)
        {
            System.out.println("geraRelatorioAG: Erro ao escrever no arquivo: " + ex.getMessage());                
        }
    }    
    
    /**
     * Agrega todos os dados do AG em um único arquivo e prapara-os como entrada no R.
     * Imprime ao fim do arquivo de dados os plots da média das execuções a serem
     * calculadas no R. 
     * @param melhorFitness Fitness do melhor individuo de todas as execuções.
     */
    private void juntaDadosAG(double melhorFitness)
    {
        try {
            int i;
            FileWriter fw = new FileWriter("logs/Base" + NUM_DB + "_DadosAG" + NUM_PROV + "x" + NUM_REQ + ".txt", false);
            BufferedWriter bw = new BufferedWriter(fw);
            FileReader fr;
            BufferedReader br;
            
            for(i = 1; i <= MAX_EXECUCOES; i++)
            {
                ////////////////////////////////////////////////////////////////////////////////
                fr = new FileReader("temps/FitMelhorAG" + i + ".txt");
                br = new BufferedReader(fr);            
                // # # # # # Processo de normalizacao dos melhores fitness # # # # #            
                /*String linha = br.readLine();   
                String[] valores = linha.split(",");
                double min = Double.MAX_VALUE, max = 0.00;  
                double[] valoresReais = new double[valores.length]; 
                int lastPos = valoresReais.length - 1;
                for(j = 0; j < valores.length; j++)
                {
                    valoresReais[j] = Double.parseDouble(valores[j]);
                    if(valoresReais[j] < min)  min = valoresReais[j];
                    if(valoresReais[j] > max)  max = valoresReais[j];
                }
                linha = "";
                for(j = 0; j < valoresReais.length - 1; j++)
                {
                    //valoresReais[j] = arredonda(valoresReais[j] - min, 4);
                    valoresReais[j] = arredonda((1.0/(max - min)) * valoresReais[j] - (1.0/(max - min)) * min, 4);
                    linha += valoresReais[j] + ",";                
                }
                //valoresReais[lastPos] = arredonda(valoresReais[lastPos] - min, 4);
                valoresReais[lastPos] = arredonda((1.0/(max - min)) * valoresReais[lastPos] - (1.0/(max - min)) * min, 4);
                linha += valoresReais[lastPos]; */           
                // # # # # # Fim do processo de normalizacao dos melhores  # # # # #
                bw.append("fitMelhorAG" + i + " <- c(" + br.readLine() + ");");            
                //bw.append("fitMelhorAG" + i + " <- c(" + linha + ");");            
                bw.newLine();
                br.close();
                fr.close();
                ////////////////////////////////////////////////////////////////////////////////
                fr = new FileReader("temps/FitMedioAG" + i + ".txt");
                br = new BufferedReader(fr); 
                // # # # # # Processo de normalizacao dos fitness medios # # # # # #
                /*linha = br.readLine();   
                valores = linha.split(",");
                min = Double.MAX_VALUE;
                max = 0.00;  
                valoresReais = new double[valores.length]; 
                lastPos = valoresReais.length - 1;
                for(j = 0; j < valores.length; j++)
                {
                    valoresReais[j] = Double.parseDouble(valores[j]);
                    if(valoresReais[j] < min)  min = valoresReais[j];
                    if(valoresReais[j] > max)  max = valoresReais[j];
                }
                linha = "";
                for(j = 0; j < valoresReais.length - 1; j++)
                {
                    //valoresReais[j] = arredonda(valoresReais[j] - min, 4);
                    valoresReais[j] = arredonda((1.0/(max - min)) * valoresReais[j] - (1.0/(max - min)) * min, 4);
                    linha += valoresReais[j] + ",";                
                }
                //valoresReais[lastPos] = arredonda(valoresReais[lastPos] - min, 4);
                valoresReais[lastPos] = arredonda((1.0/(max - min)) * valoresReais[lastPos] - (1.0/(max - min)) * min, 4);
                linha += valoresReais[lastPos];  */          
                // # # # # # Fim do processo de normalizacao dos medios # # # # # #
                bw.append("fitMedioAG" + i + " <- c(" + br.readLine() + ");");            
                //bw.append("fitMedioAG" + i + " <- c(" + linha + ");");            
                bw.newLine();
                br.close();
                fr.close();
                ////////////////////////////////////////////////////////////////////////////////                
                fr = new FileReader("temps/VariabilidadeAG" + i + ".txt");
                br = new BufferedReader(fr); 
                bw.append("variabilidadeAG" + i + " <- c(" + br.readLine() + ");");
                bw.newLine();
                bw.newLine();
                br.close();
                fr.close();
                ////////////////////////////////////////////////////////////////////////////////
                /*fr = new FileReader("temps/MNDF_AG" + i + ".txt");
                br = new BufferedReader(fr); 
                bw.append("mndf" + i + " <- c(" + br.readLine() + ");");            
                bw.newLine();
                bw.newLine();
                br.close();
                fr.close(); */ 
                ////////////////////////////////////////////////////////////////////////////////
            } 
            // Pos processamentos finais
            bw.append("geracoesAG <- c(seq(0, " + MAX_GERACOES + "));");
            bw.newLine();
            /////////////////////////////////////
            String fitMelhor = "fitMelhorAG <- (";
            String fitMedio = "fitMedioAG <- (";
            String variabilidade = "variabilidadeAG <- (";            
            for(i = 1; i < MAX_EXECUCOES; i++)
            {                
                fitMelhor += "fitMelhorAG" + i + " + ";
                fitMedio += "fitMedioAG" + i + " + ";
                variabilidade += "variabilidadeAG" + i + " + ";
            }            
            fitMelhor += "fitMelhorAG" + MAX_EXECUCOES + ")/" + MAX_EXECUCOES + ";";
            fitMedio += "fitMedioAG" + MAX_EXECUCOES + ")/" + MAX_EXECUCOES + ";";
            variabilidade += "variabilidadeAG" + MAX_EXECUCOES + ")/" + MAX_EXECUCOES + ";";
            bw.append(fitMelhor);
            bw.newLine();
            bw.append(fitMedio);
            bw.newLine();
            bw.append(variabilidade);
            bw.newLine();
            bw.newLine();
            // Normalizacao dos valores no R
            //bw.append("maxAG <- max(max(fitMelhorAG), max(fitMedioAG));");
            //bw.append("maxAG <- " + arredonda(melhorFitness, 4) + ";");
            //bw.newLine();
            //bw.append("minAG <- min(min(fitMelhorAG), min(fitMedioAG));");
            //bw.newLine();
            //bw.append("fitMelhorNormAG <- ((1.0/(maxAG - minAG)) * fitMelhorAG - (1.0/(maxAG - minAG)) * minAG);");
            //bw.newLine();
            //bw.append("fitMedioNormAG <- ((1.0/(maxAG - minAG)) * fitMedioAG - (1.0/(maxAG - minAG)) * minAG);");
            //bw.newLine();
            //bw.newLine();
            // Função plot no R
            // Carregar arquivo: source("..//Desktop//DadosAG.txt");
            // plot(vetor de dados, type="<p/i/o>", col="<COR>", xlab="<nome eixo x>", xlim/ylim = c(.., ..)
            //   ylab="<nome eixo y>", main="<Título>", sub = "<Subtitulo>")
            // lines(dados, type = "p/i/o", col = "COR") -> Adiciona uma curva
            // https://docs.ufpr.br/~aanjos/CE231/web/apostila.html
            // http://www.w3ii.com/pt/r/r_line_graphs.html
            // VARS: geracoes, fitMelhor, fitMedio, variabilidade e mndf    
            // SALVAR imagem: pdf("nome.pdf"); ou jpeg("nome.jpg"); ou png("nome.jpg");
            //                  comando para o grafico
            //                dev.off();
            bw.append("pdf(\"../Desktop/AG_Converg" + NUM_PROV + "_Req" + NUM_REQ + ".pdf\");");            
            bw.newLine(); // fitMelhorNormAG
            bw.append("plot(fitMelhorAG ~ geracoesAG, type='l', col='blue', "
                    + "xlab='Generations', ylab='Fitness', ylim=c(0.94,1.0), "
                    + "main='Convergence of the mean of " + MAX_EXECUCOES + " executions (GA)');");            
            bw.newLine(); // fitMedioNormAG
            bw.append("lines(fitMedioAG, type = 'l', col = 'red');");  
            bw.newLine();
            bw.append("dev.off();");
            bw.newLine();
            bw.newLine();
            bw.append("pdf(\"../Desktop/AG_Var" + NUM_PROV + "_Req" + NUM_REQ + ".pdf\");");
            bw.newLine();
            bw.append("plot(variabilidadeAG ~ geracoesAG, type='l', col='blue', xlab='Generations', "
                    + "ylab='Genotypic Variability', ylim=c(0,1), "
                    + " main='Genetic variability of the mean of " + MAX_EXECUCOES + " executions (GA)');");
            bw.newLine();  
            bw.append("dev.off();");
            bw.newLine();
            /*
            bw.append("pdf(\"../Desktop/AG_MNDF" + NUM_PROV + "_Req" + NUM_REQ + ".pdf\");");
            bw.newLine();
            bw.append("plot(mndf~geracoes, type='o', col='blue', xlab='Generations', "
                    + "ylab='MNDF value', main='Genetic variability (MNDF)');");
            bw.newLine();  
            bw.append("dev.off();");
            bw.newLine(); 
            bw.newLine(); */
            bw.close();
        }
        catch (IOException ex)
        {
            System.err.println("juntaDadosAG: Erro com os arquivos: " + ex.getMessage());
        }
    }    
}