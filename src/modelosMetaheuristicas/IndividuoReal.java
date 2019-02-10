/*
 * UDESC - Universidade do Estado de Santa Catarina
 * Aluno: Lucas Borges de Moraes
 * Curso: Mestrado em Computação Aplicada - MCA 
 * Orientadores: Adriano Fiorese e Rafael Parpinelli
 */
package modelosMetaheuristicas;

import java.util.Arrays;
import java.util.Random;
import static java.lang.Math.exp;

public class IndividuoReal 
{
    /** Vetor de variáveis do individuo, codificadas em numeros reais. */
    private double[] codificacao; 
    /** Vetor de variáveis codificadas em binários de tamanho 1 relativa a codificacao real.  */
    private boolean[] codificacaoBin;
    /** Valor da função de fitness desse individuo. */
    private double fitness;    
    /** Indica esse invididuo foi penalizado ou não. */
    private boolean penalizado; 
    /** Gerador randomico da classe. */
    private static final Random gerador = new Random();
    /**
     * Cria um novo indivíduo real vazio.   
     * @param numVars Número de variáveis do individuo, deve ser positivo e diferente de zero.     
     */       
    public IndividuoReal(int numVars)
    {
        if(numVars <= 0)
            throw new IllegalArgumentException("O numero de variaveis deve ser positivo!");               
        
        this.codificacao = new double[numVars];
        this.codificacaoBin = new boolean[numVars];
        this.fitness = 0.00;
        this.penalizado = false;
        for(int i = 0; i < numVars; i++)
        {
            this.codificacao[i] = 0.00;
            this.codificacaoBin[i] = false;
        }
    }
    /**
     * Cria um novo individuo real com a codificação passada.   
     * @param codificacao Vetor de codificação do individuo.     
     */       
    public IndividuoReal(double[] codificacao)
    {
        if(codificacao == null || codificacao.length <= 0)
            throw new IllegalArgumentException("Vetor de codificação invalido!");
        
        this.codificacao = codificacao;
        this.codificacaoBin = new boolean[codificacao.length];
        this.fitness = 0.00;
        this.penalizado = false;
    }
        
    // # # # # # # # # # #  GETTERS  # # # # # # # # # #    
    /** @return O valor da função de fitness do individuo. */
    public double getFitness() 
    {
        return fitness;
    }    
    /** @return Um booleano que indica se foi penalizado ou não, por infringir restrições. */
    public boolean isPenalizado() 
    {
        return penalizado;
    }
    /** @return Obtem-se o vetor inteiro de codificacao real desse indivíduo.  */
    public double[] getCod()
    {
        return codificacao;
    }
    /** @return Obtem-se o tamanho do vetor de codificação desse indivíduo. */
    public int getTamCod()
    {
        return codificacao.length;
    }
    /**     
     * @param pos Posição da codificação do individuo.
     * @return Obtem-se a codificação desse indivíduo na posição passada.
     */
    public double getCodPos(int pos)
    {
        return codificacao[pos];
    }
    /** @return Obtem-se o vetor inteiro de codificacao binaria desse indivíduo.  */
    public boolean[] getCodBin()
    {
        return codificacaoBin;
    }
    /** @return Obtem-se o tamanho do vetor de codificação binaria desse indivíduo. */
    public int getTamCodBin()
    {
        return codificacaoBin.length;
    }
    /**     
     * @param pos Posição da codificação binaria do individuo.
     * @return Obtem-se a codificação desse indivíduo na posição passada.
     */
    public boolean getCodBinPos(int pos)
    {
        return codificacaoBin[pos];
    }
    
    // # # # # # # # # # #  SETTERS  # # # # # # # # # #    
    /**
     * Modifica o valor de fitness do indivíduo.
     * @param fitness Valor do novo fitness, deve ser positivo.
     */
    public void setFitness(double fitness)    
    {
        this.fitness = fitness;  
    }    
    /**
     * Modifica o booleano que diz se o individuo foi penalizado por infringir restrições.
     * @param penalizado True = Penalizado; False = Não penalizado.
     */
    public void setPenalizado(boolean penalizado)
    {
        this.penalizado = penalizado;
    } 
    /**
     * Muda o vetor de codificação real do individuo na posição passada pelo booleano passado.
     * @param pos Posição que se deseja mudar a codificação.
     * @param newCod Novo valor da codificação.
     */
    public void setCodPos(int pos, double newCod) 
    {
        this.codificacao[pos] = newCod;       
    }  
    /**
     * Muda o vetor de codificação real do individuo pela codificação passada.
     * @param codificacao Nova codificação.
     */
    public void setCodificacao(double[] codificacao) 
    {
        this.codificacao = codificacao;       
    }
    /**
     * Muda o vetor de codificação binaria do individuo na posição passada pelo booleano passado.
     * @param pos Posição que se deseja mudar a codificação.
     * @param newCod Novo valor da codificação.
     */
    public void setCodBinPos(int pos, boolean newCod) 
    {
        this.codificacaoBin[pos] = newCod;       
    }  
    /**
     * Muda o vetor de codificação binaria do individuo pela codificação passada.
     * @param codificacao Nova codificação.
     */
    public void setCodificacao(boolean[] codificacao) 
    {
        this.codificacaoBin = codificacao;       
    }
    
    /** @return Um individuo contendo a versao binaria desse individuo real corrente. */
    public final IndividuoBin toIndBin()
    {
        IndividuoBin ind;
        atualizaCodBin();
        ind = new IndividuoBin(codificacaoBin);
        ind.setFitness(fitness);
        ind.setPenalizado(penalizado);
        
        return ind;
    }
    // # # # # # # Discretizacao da codificacao desse individuo Real  # # # # # #
    /** Atualiza o vetor de codificacao binaria de acordo com a codificacao real desse individuo. */
    public final void atualizaCodBin()
    {
        for(int i = 0; i < codificacao.length; i++)
            codificacaoBin[i] = sigmoid(codificacao[i]);
    }
    // # # # # # # # # # #  Clona um Individuo Real  # # # # # # # # # #
    /**
     * Todo campo desse individuo se torna igual ao do indivíduo passado por parâmetro.
     * @param ind IndividuoBin a ser clonado.
     */
    public final void clone(IndividuoReal ind)
    {
        fitness = ind.fitness;        
        penalizado = ind.penalizado;
        System.arraycopy(ind.codificacao, 0, this.codificacao, 0, ind.codificacao.length);
        System.arraycopy(ind.codificacaoBin, 0, this.codificacaoBin, 0, ind.codificacaoBin.length);
    }    
    // # # # # # # # # # #  Gerador de Individuo Real Aleatório  # # # # # # # # #
    /** 
     * Gera valores aleatorios para o vetor de codificação do individuo.
     * @param min Valor minimo possivel para cada variavel real desse individuo.
     * @param max Valor maximo possivel para cada variavel real desse individuo.
     */
    public final void geraCodRand(double min, double max)
    { 
        //Random gerador = new Random();
        for(int i = 0; i < codificacao.length; i++)
            codificacao[i] = min + (max - min) * gerador.nextDouble();
        
        //if(individuoNulo()) // Vetor todo nulo nao eh invalido
           // geraCodRand(min, max); // Volta o processo
    }
    
    /**
     * Verifica se esse individuo é o nulo - Todo preenchido com "false".
     * @return True caso seja nulo, false contrário.
     */
    public final boolean individuoNulo()
    {        
        for(int i = 0; i < codificacaoBin.length; i++)
            if( codificacaoBin[i] ) // Achou um "true", nao nulo
                return false;
        
        return true;        
    }
    /**
     * Verifica se o individuo passado possue a mesma codificação real que esse individuo.
     * @param ind IndividuoReal para se verificar. 
     * @return "true" caso possuam a mesma codificação real, "false" caso contrário.
     */
    public final boolean igualCod(IndividuoReal ind)
    {
        if(codificacao.length != ind.codificacao.length)
            return false;
        
        for(int i = 0; i < ind.getTamCod(); i++)
            if(codificacao[i] != ind.codificacao[i])
                return false;
        
        return true;
    }
    /**
     * Verifica se o individuo passado possue a mesma codificação binaria que esse individuo.
     * @param ind IndividuoReal para se verificar. 
     * @return "true" caso possuam a mesma codificação binaria, "false" caso contrário.
     */
    public final boolean igualCodBin(IndividuoReal ind)
    {
        if(codificacaoBin.length != ind.codificacaoBin.length)
            return false;
        
        for(int i = 0; i < ind.getTamCod(); i++)
            if(codificacaoBin[i] != ind.codificacaoBin[i])
                return false;
        
        return true;
    }
    
    // # # # # # # # # # #  Impressão de resultados  # # # # # # # # # #
    /** Imprime somente a codificação real do individuo no console. Bom para Debugs. */
    public void imprimeCod()
    {
        for(int i = 0; i < codificacao.length; i++)        
            System.out.print(codificacao[i] + " "); 
        System.out.println();
    }
    /** Imprime somente a codificação binaria do individuo no console. Bom para Debugs. */
    public void imprimeCodBin()
    {
        for(int i = 0; i < codificacaoBin.length; i++)        
            System.out.print(codificacaoBin[i] + " "); 
        System.out.println();
    }
    
    /** Imprime todos os campos desse indivíduo no console. */   
    public void imprimeInd()
    {
        //System.out.println("Id = " + id + "\nCodificacao");
        System.out.println("Codificacao");
        for(int i = 0; i < codificacao.length; i++)        
            System.out.print(codificacao[i] + " ");            
        System.out.println();
        for(int i = 0; i < codificacaoBin.length; i++)         
            System.out.print( codificacaoBin[i] ? "1 ":"0 ");
        
        System.out.println("\nFitness = " + fitness);       
        System.out.println("Penalizado? " + (penalizado ? "Sim":"Nao"));
    }  
    /** Imprime os provedores codificados pelo individuo. */
    public void imprimeProvCod()
    {
        for(int i = 0; i < codificacao.length; i++)
            if( codificacaoBin[i] )
                System.out.print("P" + (i+1) + "\t");
        
        System.out.println();
    }
    /** @return A String correspondente os provedores codificados pelo individuo. */
    public String provCod()
    {
        String ret = "";
        for(int i = 0; i < codificacaoBin.length; i++)
            if( codificacaoBin[i] )
                ret += ("P" + (i+1) + "\t");        
        
        return ret;
    }
    /** @return A quantidade de provedores codificados pelo individuo. */
    public int numProvCod()
    {
        int i, num = 0;
        for(i = 0; i < codificacaoBin.length; i++)
            if( codificacaoBin[i] )
                num++;       
        
        return num;
    }    
    /**
     * Transforma esse indivíduo em uma String equivalente.
     * @return A representação em String desse indivíduo.
     */       
    @Override
    public String toString() 
    {
        int i;
        //String ret = "Id = " + id + "\nCodificacao\n"; 
        String ret = "Codificacao\n"; 
        for(i = 0; i < codificacao.length; i++)        
           ret += (codificacao[i] + " ");
        ret += "\n";
        for(i = 0; i < codificacaoBin.length; i++) 
            ret += (codificacaoBin[i] ? "1 ":"0 ");
        ret += "\nFitness = " + fitness;
        ret += "\nPenalizado? " + (penalizado ? "Sim":"Nao") + "\n";
        return ret;
    }
    @Override
    public boolean equals(Object obj) 
    {
        if(obj == null || this.getClass() != obj.getClass())  return false;
        if(this == obj)  return true;
        
        final IndividuoReal other = (IndividuoReal) obj;
        return Arrays.equals(this.codificacao, other.codificacao);
    }
    @Override
    public int hashCode()
    {
        int hash = 5;
        hash = 59 * hash + Arrays.hashCode(this.codificacao);
        return hash;
    }
    /**
     * Função de discretização.
     * @param f Valor real para discretizar.
     * @return O valor discretizado do real pra binario.
     */
    public final static boolean sigmoid(double f)
    {
        return ( ( ( 2.0 / (1.0 + exp(-2.0 * f) ) ) - 1.0) > 0.0 );
    }
}