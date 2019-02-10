/*
 * UDESC - Universidade do Estado de Santa Catarina
 * Aluno: Lucas Borges de Moraes
 * Curso: Mestrado em Computação Aplicada - MCA 
 * Orientadores: Adriano Fiorese e Rafael Parpinelli
 */
package metaHeuristicas;

import aplicacao.AvaliaFitness;
import modelosMetaheuristicas.PopulacaoReal;
import modelosMetaheuristicas.IndividuoReal;
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
 * Implementa a Evolução Diferencial Discretizada (EDD / DDE).
 * 
 * Pseudocódigo do Algoritmo de Evolução Diferencial Discretizada (EDD).
 * PARÂMETROS: Tamanho da população, número de variáveis, condição de parada, probabilidade de crossover e F.
 * t = 0
 * p = População inicial aleatória (TAM_POP, NUM_VARS)
 * Avaliação de fitness(p)
 * melhor = getMelhor(p)
 * Enquanto !CONDIÇÃO_DE_PARADA faça
 *      Para todo individuo i faça
 *          a = primeiro individuo para crossover
 *          b = segundo individuo aleatorio para crossover e b != a
 *          c = terceiro individuo aleatorio para crossover e c != b e c != a
 *          d = Variável aleatória de 1 a NUM_VARS
 *          Individuo y = p[i]
 *          Para tada variável j faça
 *              se OCORRER_CROSSOVER ou d = j então
 *                  y = p[a] + F * (p[b] - p[c])
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
public class EDD 
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
    /** O fator de significancia da diferença dos individuos na DE, valor entre (0, 1), aberto. */      
    private double F;
    /** Flag definindo se havera output em arquivo dos dados das geracoes do EDD ou não (false). */
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
     * @param F O fator de significancia da diferença dos individuos na DE, valor entre (0, 1), aberto.
     */
    public EDD(int NUM_PROV, int NUM_REQ, int NUM_PIS, int NUM_DB, int MAX_EXECUCOES, int MAX_GERACOES, int TAM_POP, int PROB_PR, double F)
    {
        if(NUM_PROV <= 0 || NUM_REQ <= 0 || NUM_PIS <= 0 || NUM_DB <= 0 || MAX_EXECUCOES <= 0 ||
           MAX_GERACOES <= 0 || TAM_POP <= 0 || PROB_PR < 0 || F <= 0 || F >= 1)
            throw new IllegalArgumentException("EDD: Todos os parametros devem ser maiores que zero!");
        this.NUM_PROV = NUM_PROV;
        this.NUM_REQ = NUM_REQ;
        this.NUM_PIS = NUM_PIS;
        this.NUM_DB = NUM_DB;
        this.MAX_EXECUCOES = MAX_EXECUCOES;
        this.MAX_GERACOES = MAX_GERACOES;
        this.TAM_POP = TAM_POP;
        this.PROB_PR = PROB_PR;
        this.F = F;
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
    /** @return O fator de significancia da diferença dos individuos na DE. */
    public double getF() 
    {
        return F;
    }    
    /** @return A flag indicando se ha output em arquivo dos dados das geracoes desse algoritmo não. */
    public boolean isRELATORIO()
    {
        return RELATORIO;
    }
    
    /**
     * Roda o Algoritmo da Evolução Diferencial Discretizada de busca de fitness.
     * @param RELATORIO Flag definindo se havera output em arquivo dos dados das geracoes do EDD ou não (false).
     * @param iniciais Os individuos da populacao inicial. Envie nulo para a populacao inicial ser totalmente aleatoria.
     * @return O(s) melhor(es) individuo(s) encontrado(s) em todas as execucoes.
     */
    public ArrayList<IndividuoBin> rodaEDD(boolean RELATORIO, ArrayList<double[]> iniciais)
    {
        this.RELATORIO = RELATORIO;
        // Carrega os dados
        String ArqBD = "Dados/Base" + NUM_DB + "/DADOS" + NUM_PROV + "x" + NUM_PIS + ".txt";
        String ArqReq = "Requisicoes/Base" + NUM_DB + "/REQ" + NUM_REQ + "_" + NUM_PIS + ".txt";
        AvaliaFitness af = new AvaliaFitness(ArqBD, ArqReq);
        //af.imprime();
          
        // Variaveis
        int i, j, a, b, c, d, geracao, execucao, NUM_VARS = af.getBase_dados().getNumProvedores();
        long tempoInicial, tempoFinal;
        boolean aleatorio = false;
        PopulacaoReal p; // População real corrente
        IndividuoReal melhor, novo, atual, outro1, outro2, outro3;
        IndividuoReal[] eleitos = new IndividuoReal[MAX_EXECUCOES]; // Melhores invididuos de cada execusão
        double[] tempoExecusao = new double[MAX_EXECUCOES]; // Tempo de execusão de cada execusão
        double newCod;
        
        System.out.println("# # # # # Algoritmo da Evolução Diferencial Discretizada # # # # #\n");        
        //System.out.println("Espaço de busca: " + (Math.pow(2, NUM_VARS) - 1) + "\n");
        System.out.println("NumProv: " + NUM_PROV + "\nNumReq: " + NUM_REQ + "\nNumPIs: " + NUM_PIS + "\nNumDB: " + NUM_DB);
        
        // Solucao inicial do metodo
        if(iniciais == null || iniciais.isEmpty())
            aleatorio = true;
        
        // Inicia as execuções
        execucao = 1;
        // LOOP DE EXECUÇÕES DO EDD
        while(execucao <= MAX_EXECUCOES)
        {
            // Inicio do EDD
            geracao = 0;
            novo = new IndividuoReal(NUM_VARS); // Pivô
            
            // Inicia o cronômetro
            tempoInicial = System.currentTimeMillis();
            
            // População inicial do metodo
            p = (aleatorio) ? (new PopulacaoReal(TAM_POP, NUM_VARS, -1.0, 1.0)) : // Totalmente aleatoria
                              (new PopulacaoReal(TAM_POP, NUM_VARS, -1.0, 1.0, iniciais)); // Com os individuos passados
            //System.out.println("A: " + (System.currentTimeMillis() - tempoInicial) + " ms");
            // Avaliação de fitness da população
            for(i = 0; i < TAM_POP; i++)                            
                af.fitness(p.getIndPos(i)); // A funcao de fitness ja discretiza os valores reais
              
            //System.out.println("B: " + (System.currentTimeMillis() - tempoInicial) + " ms");
            // Armazena o melhor individuo atual
            melhor = p.getMelhorInd();            
            // Armazena dados de fitness da população
            if(RELATORIO) geraRelatorioEDD(p, geracao, execucao); 
            //System.out.println("Geração inicial:"); p.imprimePopulacao();
            
            // LOOP EVOLUTIVO DO EDD
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
                    //System.out.println("C: " + (System.currentTimeMillis() - tempoInicial) + " ms");
                    do {
                        // Sorteia o primeiro individuo para perturbacao
                        a = gerador.nextInt(TAM_POP);
                    } while(a == i);
                    outro1 = p.getIndPos(a);
                    do {
                        // Sorteia o segundo individuo para perturbacao
                        b = gerador.nextInt(TAM_POP); 
                    } while(b == i || b == a);
                    outro2 = p.getIndPos(b);
                    do {
                        // Sorteia o terceiro individuo para perturbacao
                        c = gerador.nextInt(TAM_POP); 
                    } while(c == i || c == a || c == b);
                    outro3 = p.getIndPos(c);                    
                    // Posição da variável que sofrerá alteração obrigatoriamente
                    d = gerador.nextInt(NUM_VARS); 
                    //System.out.println("D: " + (System.currentTimeMillis() - tempoInicial) + " ms");
                    //System.out.println("[a: " + (a+1) + "; b: " + (b+1) + "; c: " + (c+1) + "; d: " + (d+1) + "]");
                    //System.out.println("Individuo a:");  p.getIndPos(a).imprimeInd();  System.out.println();
                    //System.out.println("Individuo b:");  p.getIndPos(b).imprimeInd();  System.out.println();
                    //System.out.println("Individuo c:");  p.getIndPos(c).imprimeInd();  System.out.println();                    
                    
                    // Para toda variável do individuo i
                    for(j = 0; j < NUM_VARS; j++)
                    {                        
                        // A variável j sofrerá perturbação ?
                        if( j == d || (gerador.nextInt(100) < PROB_PR) )
                        {   // A variável sofrerá perturbação
                            //System.out.println("Individuo " + (i+1) + " mudou na variavel " + (j+1));  
                            //System.out.println("Valor: " + novo.getCodPos(j));  
                            // novo = p[a] + F * (p[b] - p[c])
                            newCod = outro1.getCodPos(j) + F * (outro2.getCodPos(j) - outro3.getCodPos(j));
                            //newCod = p.getIndPos(a).getCodPos(j) + F * (p.getIndPos(b).getCodPos(j) - p.getIndPos(c).getCodPos(j));
                            //System.out.println("Novo valor: " + newCod);
                            // Chega os limites
                            if(newCod < -1.0)     newCod = -1.0;
                            else if(newCod > 1.0) newCod =  1.0;
                            //System.out.println("Novo valor: " + newCod);
                            novo.setCodPos(j, newCod);
                            //System.out.println("Novo valor: " + novo.getCodPos(j));
                        }
                    }
                    //System.out.println("E: " + (System.currentTimeMillis() - tempoInicial) + " ms");
                    // Avaliação de fitness para o novo indivíduo gerado
                    af.fitness(novo); // A funcao de fitness ja discretiza os valores reais                    
                    
                    //System.out.println("Novo individuo:");
                    //novo.imprimeInd();
                    //System.out.println("F: " + (System.currentTimeMillis() - tempoInicial) + " ms");
                    // Seleção gulosa: o novo individuo gerado é melhor?
                    if(novo.getFitness() > atual.getFitness())
                        atual.clone(novo); // Sustitui o melhor                    
                    //p.imprimePop();
                    //System.out.println("G: " + (System.currentTimeMillis() - tempoInicial) + " ms");
                } 
                // Armazena o melhor individuo da nova população p a cada nova geração                
                melhor = p.getMelhorInd();  
                //System.out.println("H: " + (System.currentTimeMillis() - tempoInicial) + " ms");
                // Nova geração
                geracao++;
                //System.out.println("Geração " + geracao);
                // Armazena dados de fitness da população
                if(RELATORIO) geraRelatorioEDD(p, geracao, execucao); 
            } // Fim do EDD
            //System.out.println("I: " + (System.currentTimeMillis() - tempoInicial) + " ms");
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
            // Próxima execucao do EDD
            execucao++;
        } // Fim das execuções do EDD
                
        // DEBUG
        /*for(int i = 0; i < MAX_EXECUCOES; i++)
            System.out.print(eleitos[i]);
        System.out.println();*/
        
        // # # # # Estatisticas pós execuções do EDD # # # # 
        return estatisticaFinais(eleitos, tempoExecusao);
    } // FIM
    
    /**************************************************************************/
    /********************** ESTATISTICAS E RELATORIOS *************************/
    /**************************************************************************/
    /**
     * Gera as estatisticas finais pós execuções do EDD, como fitness e tempo medio,
     * desvio padrao de fitness e tempo e a porcentagem de acertos.
     * @param eleitos Vetor com todos os invididuos eleitos pelo EDD a cada execusao.
     * @param tempoExecusao Vetor com todos os tempos de execusao de cada execusao.
     * @return O(s) melhor(es) individuo(s) encontrado(s) em todas as execucoes.
     */
    private ArrayList<IndividuoBin> estatisticaFinais(IndividuoReal[] eleitos, double[] tempoExecusao)
    {
        ArrayList<IndividuoBin> melhores = new ArrayList(); // Os melhores das execusoes para a requisição
        int i, totalExe = eleitos.length;
        double acertos = 0.00;        
        double mediaF = 0.00;
        double mediaT = 0.00;
        double desvioF = 0.00;
        //double desvioT = 0.00;
        double melhorFit = 0.00; // O Melhor fitness conhecido no momento para a requisição
        IndividuoBin aux;
        
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
                aux = eleitos[i].toIndBin();
                if(!melhores.contains(aux))
                    melhores.add(aux);
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
            fw = new FileWriter("EstatisticasEDD.txt", true);
            bw = new BufferedWriter(fw);
            // No arquivo: "EstatisticasEDD.txt"
            // Escreve: (Num req)(Num prov)(Num pis) <TAB> MEDIA fitness <TAB> DESVIO PADRAO fitness <TAB> ACERTOS (%) <TAB> Melhor fitness <TAB> TEMPO medio (ms)
            bw.append("(Req " + NUM_REQ + ")(Provs " + NUM_PROV + ")(PIs " + NUM_PIS + ")\t" + 
                mediaF + "\t" + desvioF + "\t" + acertos + "%" + "\t" + melhorFit + "\t" + arredonda(mediaT, 2));
            bw.newLine();
            bw.close();
            ////////////////////////////////////////////////////////////////////
            fw = new FileWriter("EstatisticasExecucoesEDD.txt", true);            
            bw = new BufferedWriter(fw);            
            // No arquivo: "EstatisticasExecucoesEDD.txt"
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
            System.out.println("Erro ao escrever no arquivo de estatisticas do EDD: " + ex.getMessage());
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
            juntaDadosEDD(melhorFit);
            System.out.println("Feito!\n");
        }
        return melhores;
    }
    
    /************************ FUNÇÕES DE RELATÓRIOS ***************************/
    /**
     * Função que guarda dados fundamentais para construção dos gráficos de
     * convergência e diversidade do EDD em diferentes arquivos texto.
     * Dados obtidos da população: Fitness do melhor indivíduo, fitness médio da
     * população, variabiliadade genotípica normalizada.
     * @param p População cujos dados devem ser armazenados.
     * @param geracaoAtual Número da geração atual do EDD. 
     * @param execucaoAtual Número da execução atual do EDD.
     */
    private void geraRelatorioEDD(PopulacaoReal p, int geracaoAtual, int execucaoAtual) 
    {  
        if(geracaoAtual < 0 || geracaoAtual > MAX_GERACOES || execucaoAtual < 0 || execucaoAtual > MAX_EXECUCOES)
            throw new IllegalArgumentException("geraRelatorioEDD: Dados de entrada invalidos!");
        
        FileWriter fw;
        BufferedWriter bw;
        boolean append = true; // Adiciona os dados ao fim do arquivo
        boolean ultimo = false; // Marca a ultima geracao, false = gerações intermediárias -> Esta no meio da execução do EDD
        
        if(geracaoAtual == 0) // Primeira geração -> Inicio do EDD
            append = false; // Sobreescreve o texto do arquivo
        
        if(geracaoAtual == MAX_GERACOES)
            ultimo = true; // Última geração -> Fim do EDD
        
        try {            
            fw = new FileWriter("temps/FitMelhorEDD" + execucaoAtual + ".txt", append);
            bw = new BufferedWriter(fw);
            bw.append(arredonda(p.getMelhorInd().getFitness(), 4) + (ultimo ? "":","));            
            bw.close();
            
            fw = new FileWriter("temps/FitMedioEDD" + execucaoAtual + ".txt", append);
            bw = new BufferedWriter(fw);
            bw.append(arredonda(p.getFitnessMedio(), 4) + (ultimo ? "":","));            
            bw.close();
            
            fw = new FileWriter("temps/VariabilidadeEDD" + execucaoAtual + ".txt", append);
            bw = new BufferedWriter(fw);
            bw.append(arredonda(p.variabilidadeGenetica(), 4) + (ultimo ? "":","));            
            bw.close();                  
        }
        catch (IOException ex)
        {
            System.out.println("geraRelatorioEDD: Erro ao escrever no arquivo: " + ex.getMessage());
        }
    }    
    
    /**
     * Agrega todos os dados do EDD em um único arquivo e prapara-os como entrada no R.
     * Imprime ao fim do arquivo de dados os plots da média das execuções a serem
     * calculadas no R.
     * @param melhorFitness Fitness do melhor individuo de todas as execuções.
     */
    private void juntaDadosEDD(double melhorFitness)
    {        
        try {   
            int i;
            FileWriter fw = new FileWriter("logs/Base" + NUM_DB + "_DadosEDD" + NUM_PROV + "x" + NUM_REQ + ".txt", false);            
            BufferedWriter bw = new BufferedWriter(fw);
            FileReader fr;
            BufferedReader br;
            
            for(i = 1; i <= MAX_EXECUCOES; i++)
            {
                ////////////////////////////////////////////////////////////////////////////////
                fr = new FileReader("temps/FitMelhorEDD" + i + ".txt");
                br = new BufferedReader(fr);
                bw.append("fitMelhorEDD" + i + " <- c(" + br.readLine() + ");"); 
                bw.newLine();
                br.close();
                fr.close();
                ////////////////////////////////////////////////////////////////////////////////
                fr = new FileReader("temps/FitMedioEDD" + i + ".txt");
                br = new BufferedReader(fr);                
                bw.append("fitMedioEDD" + i + " <- c(" + br.readLine() + ");"); 
                bw.newLine();
                br.close();
                fr.close();
                ////////////////////////////////////////////////////////////////////////////////                
                fr = new FileReader("temps/VariabilidadeEDD" + i + ".txt");
                br = new BufferedReader(fr); 
                bw.append("variabilidadeEDD" + i + " <- c(" + br.readLine() + ");");
                bw.newLine();
                bw.newLine();
                br.close();
                fr.close();
                ////////////////////////////////////////////////////////////////////////////////
            }
            // Pos processamentos finais
            bw.append("geracoesEDD <- c(seq(0, " + MAX_GERACOES + "));");
            bw.newLine();
            /////////////////////////////////////
            String fitMelhor = "fitMelhorEDD <- (";
            String fitMedio = "fitMedioEDD <- (";
            String variabilidade = "variabilidadeEDD <- (";
            for(i = 1; i < MAX_EXECUCOES; i++)
            {                
                fitMelhor += "fitMelhorEDD" + i + " + ";
                fitMedio += "fitMedioEDD" + i + " + ";
                variabilidade += "variabilidadeEDD" + i + " + ";
            }            
            fitMelhor += "fitMelhorEDD" + MAX_EXECUCOES + ")/" + MAX_EXECUCOES + ";";
            fitMedio += "fitMedioEDD" + MAX_EXECUCOES + ")/" + MAX_EXECUCOES + ";";
            variabilidade += "variabilidadeEDD" + MAX_EXECUCOES + ")/" + MAX_EXECUCOES + ";";            
            bw.append(fitMelhor);
            bw.newLine();
            bw.append(fitMedio);
            bw.newLine();
            bw.append(variabilidade);
            bw.newLine();
            bw.newLine();
            // Normalizacao dos valores no R
            //bw.append("maxEDD <- max(max(fitMelhorEDD), max(fitMedioEDD));");
            //bw.append("maxEDD <- " + arredonda(melhorFitness, 4) + ";");
            //bw.newLine();
            //bw.append("minEDD <- min(min(fitMelhorEDD), min(fitMedioEDD));");
            //bw.newLine();
            //bw.append("fitMelhorNormEDD <- ((1.0/(maxEDD - minEDD)) * fitMelhorEDD - (1.0/(maxEDD - minEDD)) * minEDD);");
            //bw.newLine();
            //bw.append("fitMedioNormEDD <- ((1.0/(maxEDD - minEDD)) * fitMedioEDD - (1.0/(maxEDD - minEDD)) * minEDD);");
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
            bw.append("pdf(\"../Desktop/DDE_Converg" + NUM_PROV + "_Req" + NUM_REQ + ".pdf\");");
            bw.newLine(); // fitMelhorNormEDD
            bw.append("plot(fitMelhorEDD ~ geracoesEDD, type='l', col='blue', "
                    + "xlab='Generations', ylab='Fitness', ylim=c(0.94,1.0), "
                    + "main='Convergence of the mean of " + MAX_EXECUCOES + " executions (DDE)');");            
            bw.newLine(); // fitMedioNormEDD
            bw.append("lines(fitMedioEDD, type = 'l', col = 'red');");
            bw.newLine();
            bw.append("dev.off();");
            bw.newLine();
            bw.newLine();
            bw.append("pdf(\"../Desktop/DDE_Var" + NUM_PROV + "_Req" + NUM_REQ + ".pdf\");");
            bw.newLine();
            bw.append("plot(variabilidadeEDD ~ geracoesEDD, type='l', col='blue', xlab='Generations', "
                    + "ylab='Genotypic Variability', ylim=c(0,1), "
                    + " main='Genetic variability of the mean of " + MAX_EXECUCOES + " executions (DDE)');");
            bw.newLine(); 
            bw.append("dev.off();");
            bw.newLine();
            bw.close();
        }
        catch (IOException ex)
        {
            System.err.println("juntaDadosEDD: Erro com os arquivos: " + ex.getMessage());
        }
    }
}
