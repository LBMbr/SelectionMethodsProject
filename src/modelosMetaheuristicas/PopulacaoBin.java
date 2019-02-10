/*
 * UDESC - Universidade do Estado de Santa Catarina
 * Aluno: Lucas Borges de Moraes
 * Curso: Mestrado em Computação Aplicada - MCA 
 * Orientadores: Adriano Fiorese e Rafael Parpinelli
 */
package modelosMetaheuristicas;

import java.util.ArrayList;

public class PopulacaoBin
{
    /** Tamanho da população - Quantidade máxima de individuos.  */
    private int tamPop;
    /** Número de variáveis dos individuos na população.  */
    private int numVars;     
    /** Vetor de indivíduos da população.  */
    private IndividuoBin[] individuos; 
    /** Variável que guarda a maior diversidade genotipica até agora dessa população.  */
    private double MNDF = 0.00; // Opcional
    
    /**
     * Gera uma nova população aleatória de codificação binária.
     * @param tamPop Tamanho da população.
     * @param numVars Numero de variáveis de cada individuo.
     */    
    public PopulacaoBin(int tamPop, int numVars) 
    {
        if(tamPop <= 0 || numVars <= 0)        
            throw new IllegalArgumentException("PopulacaoBin: Valores de entrada inválidos!");        
        this.tamPop = tamPop;
        this.numVars = numVars;
        this.individuos = new IndividuoBin[tamPop];
        randomPop();                       
    }
    /**
     * Gera uma nova população de codificação binária com os individuos passados
     * por parametro, se faltar individuos, aleatorios sao colocados no lugar.
     * @param tamPop Tamanho da população.
     * @param numVars Numero de variáveis de cada individuo.
     * @param init Array de individuos a serem adicionados na populacao.
     */    
    public PopulacaoBin(int tamPop, int numVars, ArrayList<boolean[]> init) 
    {
        if(tamPop <= 0 || numVars <= 0 || init == null)
            throw new IllegalArgumentException("PopulacaoBin: Valores de entrada inválidos!");
        this.tamPop = tamPop;
        this.numVars = numVars;
        this.individuos = new IndividuoBin[tamPop];
        int i, flag = 0, numInd = (init.size() > tamPop) ? tamPop : init.size();
        for(i = 0; i < numInd; i++)
            if(init.get(i).length != numVars) flag++; // Codificacao invalida
            else  this.individuos[i] = new IndividuoBin(init.get(i));            
        
        numInd -= flag;
        for(i = numInd; i < tamPop; i++)
        {
            this.individuos[i] = new IndividuoBin(numVars);
            this.individuos[i].geraCodRand();
        }
    }
    // # # # # # # # # # #  GETTERS  # # # # # # # # # #
    /** @return O tamanho da população.   */   
    public int getTamPop()
    {
        return tamPop;
    }
    /** @return O número de varáveis dos indivíduos na população.   */
    public int getNumVars()
    {
        return numVars;
    }  
    /**
     * @param pos Posição do individuo desejado.
     * @return O individuo na posição passada.   */
    public IndividuoBin getIndPos(int pos)
    {
        return individuos[pos];
    }
    /** @return O tamanho máximo dessa população.   */   
    public int getTamPopMax()
    {
        return individuos.length;
    }
    /** @return O MNDF dessa populacao.   */
    public double getMNDF() 
    {
        return MNDF;
    }
    // # # # # # # # # # #  SETTERS  # # # # # # # # # #
    /**
     * Modifica o individuo binario na posição passada.
     * @param ind Novo individuo binario.
     * @param pos Posicao do individuo binario a ser trocado.
     */
    public void setIndPos(IndividuoBin ind, int pos)
    {
        this.individuos[pos].clone(ind);
        //this.individuos[pos].setId(pos+1);
    }    
    /**
     * Modifica o valor de MNDF dessa população. MNDF > 0
     * @param MNDF O novo valor.
     */
    public void setMNDF(double MNDF)
    {
        if(MNDF <= 0.00)
            throw new IllegalArgumentException("Valor de MNDF inválido!");
        // MNDF é sempre crescente
        if(MNDF > this.MNDF)
            this.MNDF = MNDF;
    }
    
    // # # # # # # # # # #  Torna essa população com individuos aleatórios  # # # # # # # # # #
    /**
     * Gera uma nova população aleatória.
     */
    public final void randomPop()
    {
        for(int i = 0; i < tamPop; i++)
        {
            //individuos[i] = new IndividuoBin(i+1, numVars);
            individuos[i] = new IndividuoBin(numVars);
            individuos[i].geraCodRand();            
        }
    }
    
    /** @return A maior quantidade de provedores codificados por um individuo dessa populacao. */
    public int maxNumProvCod()
    {
        int i, num, maxNum = 0;
        
        for(i = 0; i < individuos.length; i++)
        {
            num = individuos[i].numProvCod();
            if(num > maxNum)
                maxNum = num;
        } 
        return maxNum;
    }
    // # # # # # # # # # #  Clona a população  # # # # # # # # # #
    /**
     * Todo campo dessa população se torna igual a da população passada por parâmetro
     * @param p População a ser clonada
     */
    public final void clone(PopulacaoBin p)
    {
        tamPop = p.tamPop;
        numVars = p.numVars;
        for(int i = 0; i < p.individuos.length; i++)
            individuos[i].clone(p.individuos[i]);
        MNDF = p.MNDF;
    }
    
    // # # # # # # # # #  Obtêm melhor individuo da população  # # # # # # # # #
    /**     
     * @return O melhor individuo da população baseado em seu fitness.
     */ 
    public IndividuoBin getMelhorInd()
    {
        IndividuoBin melhor = new IndividuoBin(numVars);
        double melhorFitness = 0.00;
        double fitnessAtual;
        
        for(int i = 0; i < individuos.length; i++)
        {
            fitnessAtual = individuos[i].getFitness();
            if(fitnessAtual > melhorFitness)
            {
                melhorFitness = fitnessAtual;
                melhor.clone(individuos[i]);
            }
        }
        return melhor;
    }
    // # # # # # # # # #  Obtêm pior individuo da população  # # # # # # # # #
    /**     
     * @return O pior individuo da população baseado em seu fitness.
     */ 
    public IndividuoBin getPiorInd()
    {
        IndividuoBin pior = new IndividuoBin(numVars);
        double piorFitness = Double.MAX_VALUE;
        double fitnessAtual;
        
        for(int i = 0; i < individuos.length; i++)
        {
            fitnessAtual = individuos[i].getFitness();
            if(fitnessAtual < piorFitness)
            {
                piorFitness = fitnessAtual;
                pior.clone(individuos[i]);
            }
        }
        return pior;
    }
    // # # # # # # # # # #  Fitness Médio  # # # # # # # # # #
    /**    
     * @return O fitness medio (cru) dessa populacao.
     */
    public double getFitnessMedio()
    {        
        double media = 0.00;
        
        for(int i = 0; i < individuos.length; i++)        
            media += individuos[i].getFitness();
        
        return (media / individuos.length);        
    }    
    // # # # # # # # # # #  Eletismo  # # # # # # # # # #
    /**
     * Implementa o eletismo nessa população, substituindo o pior individuo da
     * população pelo individuo "eleito", caso seu fitness seja realmente melhor.
     * @param eleito IndividuoBin a ser inserido na populacao. 
     * @return Um booleano indicando se holve substituição (true) ou não (false).
     */
    public boolean eletismo(IndividuoBin eleito)
    {
        // O individuo deve ser válido e compatível com a população            
        if(eleito.getTamCod() == numVars)
        {
            // # # # # # # # # ELETISMO 1 - Menor agressivo # # # # # # # #
            // Esse individuo é realmente melhor que o primeiro individuo?
            /*if(eleito.getFitness() > individuos[0].getFitness())
            {
                individuos[0].clone(eleito);
                //individuos[0].setId(1);
                return true;
            }*/
            // # # # # # # # # ELETISMO 3 - Mais agressivo # # # # # # # #
            // Substitui o primeiro individuo com menor fitness que o eleito
            /*for(int i = 0; i < individuos.length; i++)
            {
                if(eleito.getFitness() > individuos[i].getFitness())
                {
                    individuos[i].clone(eleito);
                    //individuos[i].setId(i + 1);
                    return true;                    
                }
            }*/
            // # # # # # # # # ELETISMO 3 - Mais agressivo ainda # # # # # # # #
            // Busca o indice do pior individuo            
            int ind_pior = -1;
            double piorFitness = Double.MAX_VALUE;
            for(int i = 0; i < individuos.length; i++)
            {
                if(individuos[i].getFitness() < piorFitness)
                {
                    ind_pior = i;
                    piorFitness = individuos[i].getFitness();
                }
            }
            // Esse individuo é realmente melhor que o pior individuo?
            if(eleito.getFitness() > individuos[ind_pior].getFitness())
            {
                individuos[ind_pior].clone(eleito);
                //individuos[ind_pior].setId(ind_pior + 1);
                return true;
            }
        }
        return false;
    }
    // # # # # # # # # # #  Diversidade genetípica  # # # # # # # # # #
    /**
     * Calcula a variabilidade genética normalizada da população binária.
     * @return Um real associado a variabilidade genotípica dessa população.
     */
    public double variabilidadeGenetica()
    {   
        double variabilidade = 0.00;
        
        for(int i = 0; i < tamPop - 1; i++)
        {
            //System.out.println("i: " + i);
            for(int j = i + 1; j < tamPop; j++)
            {  
                //System.out.println("   j: " + j);
                for(int k = 0; k < numVars; k++)
                { // Distancia de Haming
                    //System.out.println("      k: " + k + "(" + individuos[i].getCodPos(k) + ", " + individuos[j].getCodPos(k) + ")");
                    if(individuos[i].getCodPos(k) != individuos[j].getCodPos(k)) 
                        variabilidade++;                    
                }
                //System.out.println("      " + variabilidade);
            }  
        }
        //System.out.println("Soma: " + variabilidade);
        variabilidade = (variabilidade / tamPop);
        //System.out.println("Variabilidade: " + variabilidade);
        if(variabilidade > MNDF)
            MNDF = variabilidade; // MNDF = Sempre o maior valor de variabilidade
        return (variabilidade / MNDF); // Normalização entre 0 e 1
    }
    // # # # # # # # # # #  Impressão de resultados  # # # # # # # # # #
    /** Imprime no console os abributos da população de todos os seus indivíduos. */ 
    public void imprimePopulacao()
    {
        System.out.println("############################### POPULACAO ###############################");
        System.out.println("Numero de individuos = " + tamPop + "\nNumero de variaveis = " + numVars);
        System.out.println("-----------------------------------------------------------------------");
        for(int i = 0; i < individuos.length; i++)
        {
            individuos[i].imprimeInd();
            System.out.println("-----------------------------------------------------------------------");
        }               
        System.out.println("################################# FIM ##################################");
    }
}
