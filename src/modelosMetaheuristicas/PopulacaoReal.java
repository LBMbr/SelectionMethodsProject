/*
 * UDESC - Universidade do Estado de Santa Catarina
 * Aluno: Lucas Borges de Moraes
 * Curso: Mestrado em Computação Aplicada - MCA 
 * Orientadores: Adriano Fiorese e Rafael Parpinelli
 */
package modelosMetaheuristicas;

import java.util.ArrayList;

public class PopulacaoReal
{
    /** Tamanho da população - Quantidade máxima de individuos.  */
    private int tamPop;
    /** Número de variáveis dos individuos na população.  */
    private int numVars;     
    /** Vetor de indivíduos da população.  */
    private IndividuoReal[] individuos; 
    /** Variável que guarda a maior diversidade genotipica até agora dessa população.  */
    private double MNDF = 0.00; // Opcional
    
    /**
     * Gera uma nova população aleatória de codificação real.   
     * @param tamPop Tamanho da população.    
     * @param numVars Numero de variáveis de cada individuo.  
     * @param minVal Valor minimo possivel para cada variavel real dos individuos dessa populacao.
     * @param maxVal Valor maximo possivel para cada variavel real dos individuos dessa populacao.
     */    
    public PopulacaoReal(int tamPop, int numVars, double minVal, double maxVal) 
    {
        if(tamPop <= 0 || numVars <= 0)        
            throw new IllegalArgumentException("PopulacaoReal: Valores de entrada inválidos!");
        if(minVal > maxVal)
            throw new IllegalArgumentException("PopulacaoReal: O intervalo de valores passado eh inválido!");
        this.tamPop = tamPop;
        this.numVars = numVars; 
        this.individuos = new IndividuoReal[tamPop]; 
        randomPop(minVal, maxVal);        
    }
    /**
     * Gera uma nova população de codificação real com os individuos passados
     * por parametro, se faltar individuos, aleatorios sao colocados no lugar.
     * @param tamPop Tamanho da população.
     * @param numVars Numero de variáveis de cada individuo.
     * @param minVal Valor minimo possivel para cada variavel real dos individuos dessa populacao.
     * @param maxVal Valor maximo possivel para cada variavel real dos individuos dessa populacao.
     * @param init Array de individuos a serem adicionados na populacao.
     */    
    public PopulacaoReal(int tamPop, int numVars, double minVal, double maxVal, ArrayList<double[]> init) 
    {
        if(tamPop <= 0 || numVars <= 0 || init == null)
            throw new IllegalArgumentException("PopulacaoBin: Valores de entrada inválidos!");
        if(minVal > maxVal)
            throw new IllegalArgumentException("PopulacaoReal: O intervalo de valores passado eh inválido!");
        this.tamPop = tamPop;
        this.numVars = numVars;
        this.individuos = new IndividuoReal[tamPop];
        int i, flag = 0, numInd = (init.size() > tamPop) ? tamPop : init.size();
        for(i = 0; i < numInd; i++)
            if(init.get(i).length != numVars) flag++; // Codificacao invalida
            else  this.individuos[i] = new IndividuoReal(init.get(i));            
        
        numInd -= flag;
        for(i = numInd; i < tamPop; i++)
        {
            this.individuos[i] = new IndividuoReal(numVars);
            this.individuos[i].geraCodRand(minVal, maxVal);
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
    public IndividuoReal getIndPos(int pos)
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
     * Modifica o individuo real na posição passada.
     * @param ind Novo individuo real.
     * @param pos Posicao do individuo real a ser trocado.
     */
    public void setIndPos(IndividuoReal ind, int pos)
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
     * @param min Valor minimo possivel para cada variavel real dos individuos dessa populacao.
     * @param max Valor maximo possivel para cada variavel real dos individuos dessa populacao.
     */
    public final void randomPop(double min, double max)
    {  
        for(int i = 0; i < tamPop; i++)
        {
            individuos[i] = new IndividuoReal(numVars);
            individuos[i].geraCodRand(min, max);            
        }
    }
    /** @return A maior quantidade de provedores codificados por um individuo dessa populacao. */
    /*public int maxNumProvCod()
    {
        int i, num, maxNum = 0;
        
        for(i = 0; i < individuos.length; i++)
        {
            num = individuos[i].numProvCod();
            if(num > maxNum)
                maxNum = num;
        } 
        return maxNum;
    }*/
    /** @return Uma populacao binaria contendo a versao binaria dessa populacao real corrente. */
    /*public final PopulacaoBin toBinPop()
    {
        PopulacaoBin pop = new PopulacaoBin(tamPop, numVars);
        for(int i = 0; i < tamPop; i++)
            pop.setIndPos(individuos[i].toIndBin(), i);
        
        return pop;
    }  */  
    // # # # # # # # # # #  Clona a população  # # # # # # # # # #
    /**
     * Todo campo dessa população se torna igual a da população passada por parâmetro.
     * @param p População a ser clonada.
     */
    public final void clone(PopulacaoReal p)
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
    public IndividuoReal getMelhorInd()
    {
        IndividuoReal melhor = new IndividuoReal(numVars);
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
    public IndividuoReal getPiorInd()
    {
        IndividuoReal pior = new IndividuoReal(numVars);
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
                    if(individuos[i].getCodBinPos(k) != individuos[j].getCodBinPos(k)) 
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
