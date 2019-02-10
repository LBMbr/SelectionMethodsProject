/*
 * UDESC - Universidade do Estado de Santa Catarina
 * Aluno: Lucas Borges de Moraes
 * Curso: Mestrado em Computação Aplicada - MCA 
 * Orientadores: Adriano Fiorese e Rafael Parpinelli
 */
package modelosMetaheuristicas;

import java.util.Arrays;
import java.util.Random;

public class IndividuoBin
{
    /** Vetor de variáveis do individuo, codificadas em binários de tamanho 1.  */
    private boolean[] codificacao; 
    /** Valor da função de fitness desse individuo.  */
    private double fitness;    
    /** Indica esse invididuo foi penalizado ou não.  */
    private boolean penalizado;       
    /** Gerador randomico da classe. */
    private static final Random gerador = new Random();
    /**
     * Cria um novo indivíduo binário vazio.   
     * @param numVars Número de variáveis do individuo, deve ser positivo e diferente de zero.     
     */       
    public IndividuoBin(int numVars)
    {
        if(numVars <= 0)       
            throw new IllegalArgumentException("O numero de variaveis deve ser positivo!");               
        
        this.codificacao = new boolean[numVars];
        this.fitness = 0.00;
        this.penalizado = false;
        for(int i = 0; i < numVars; i++)
            this.codificacao[i] = false;
    }
    /**
     * Cria um novo individuo binário com a codificação passada.
     * @param codificacao Vetor de codificação do individuo.
     */       
    public IndividuoBin(boolean[] codificacao)
    {
        if(codificacao == null || codificacao.length <= 0)       
            throw new IllegalArgumentException("Vetor de codificação invalido!");               
        
        this.codificacao = codificacao;
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
    /** @return Obtem-se o vetor inteiro de codificacao desse indivíduo.  */
    public boolean[] getCod()
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
    public boolean getCodPos(int pos)
    {
        return codificacao[pos];
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
     * Muda o vetor de codificação do individuo na posição passada pelo booleano passado.
     * @param pos Posição que se deseja mudar a codificação.
     * @param newCod Novo valor da codificação.
     */
    public void setCodPos(int pos, boolean newCod) 
    {
        this.codificacao[pos] = newCod;       
    }  
    /**
     * Muda o vetor de codificação do individuo pela codificação passada.
     * @param codificacao Nova codificação.
     */
    public void setCodificacao(boolean[] codificacao) 
    {
        this.codificacao = codificacao;       
    }
    
    // # # # # # # # # # #  Clona um Individuo Binario  # # # # # # # # # #
    /**
     * Todo campo desse individuo se torna igual ao do indivíduo passado por parâmetro.
     * @param ind IndividuoBin a ser clonado.
     */
    public final void clone(IndividuoBin ind)
    {
        //id = ind.id;
        fitness = ind.fitness;        
        penalizado = ind.penalizado;
        System.arraycopy(ind.codificacao, 0, this.codificacao, 0, ind.codificacao.length);
    }  
    // # # # # # # # # # #  Gerador de Individuo Binario Aleatório  # # # # # # # #
    /** Gera valores aleatorios para o vetor de codificação do individuo. */
    public final void geraCodRand()
    {        
        //Random gerador = new Random();        
        for(int i = 0; i < codificacao.length; i++)       
            codificacao[i] = gerador.nextBoolean();             
        
        if(individuoNulo()) // Vetor todo nulo nao eh invalido
            geraCodRand(); // Volta o processo
    }    
    /**
     * Verifica se esse individuo é o nulo - Todo preenchido com "false".
     * @return True caso seja nulo, false contrário.
     */
    public final boolean individuoNulo()
    {        
        for(int i = 0; i < codificacao.length; i++)
            if( codificacao[i] ) // Achou um "true", nao nulo
                return false;
        
        return true;
    }
    /**
     * Verifica se o individuo passado possue a mesma codificação que esse individuo.
     * @param ind IndividuoBin para se verificar. 
     * @return "true" caso possuam a mesma codificação, "false" caso contrário.
     */
    public final boolean igualCod(IndividuoBin ind)
    {
        if(codificacao.length != ind.codificacao.length)
            return false;
        
        for(int i = 0; i < ind.getTamCod(); i++)
            if(codificacao[i] != ind.codificacao[i])
                return false;
        
        return true;
    }
    
    // # # # # # # # # # #  Impressão de resultados  # # # # # # # # # #
    /** Imprime somente a codificação do individuo no console. Bom para Debugs. */
    public void imprimeCod()
    {
        for(int i = 0; i < codificacao.length; i++)        
            System.out.print(codificacao[i] ? "1 ":"0 "); 
        System.out.println();
    }
    
    /** Imprime todos os campos desse indivíduo no console. */   
    public void imprimeInd()
    { 
        //System.out.println("Id = " + id + "\nCodificacao");
        System.out.println("Codificacao");
        for(int i = 0; i < codificacao.length; i++)        
            System.out.print(codificacao[i] ? "1 ":"0 "); 
        
        System.out.println("\nFitness = " + fitness);       
        System.out.println("Penalizado? " + (penalizado ? "Sim":"Nao"));
    }  
    /** Imprime os provedores codificados pelo individuo. */
    public void imprimeProvCod()
    {   
        for(int i = 0; i < codificacao.length; i++)
            if(codificacao[i])
                System.out.print("P" + (i+1) + "\t");
        
        System.out.println();        
    }
    /** @return A String correspondente aos provedores codificados pelo individuo. */
    public String provCod()
    {
        String ret = "";
        for(int i = 0; i < codificacao.length; i++)
            if(codificacao[i])
                ret += ("P" + (i+1) + "\t");        
        
        return ret;
    }
    /** @return A quantidade de provedores codificados pelo individuo. */
    public int numProvCod()
    {
        int i, num = 0;
        for(i = 0; i < codificacao.length; i++)
            if(codificacao[i])
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
        //String ret = "Id = " + id + "\nCodificacao\n"; 
        String ret = "Codificacao\n"; 
        for(int i = 0; i < codificacao.length; i++)        
           ret += (codificacao[i] ? "1 ":"0 ");
        
        ret += "\nFitness = " + fitness;
        ret += "\nPenalizado? " + (penalizado ? "Sim":"Nao") + "\n";
        return ret;
    }
    @Override
    public boolean equals(Object obj) 
    {
        if(obj == null || this.getClass() != obj.getClass())  return false;
        if(this == obj)  return true;
        
        final IndividuoBin other = (IndividuoBin) obj;
        return Arrays.equals(this.codificacao, other.codificacao);
    }
    @Override
    public int hashCode()
    {
        int hash = 5;
        hash = 89 * hash + Arrays.hashCode(this.codificacao);
        return hash;
    }
}