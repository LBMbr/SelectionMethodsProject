/*
 * UDESC - Universidade do Estado de Santa Catarina
 * Aluno: Lucas Borges de Moraes
 * Curso: Mestrado em Computação Aplicada - MCA 
 * Orientadores: Adriano Fiorese e Rafael Parpinelli
 */
package modelos;

import java.util.ArrayList;
import java.util.Objects;

public class Provedor 
{
    /** Nome identificador (todo com letras minusculas e sem espaçamentos) do provedor utilizado. */
    private String nome = "";
    /** Array de PIs disbonibilizados pelo provedor. */
    private final ArrayList<PI> pis = new ArrayList();
    /** Custo associado ao uso desse provedor. */
    private double custo = 0.00;
    
    // # # # # # # # # Construtores # # # # # # # #
    /**
     * 
     * @param nome
     * @param pis
     * @param custo 
     */
    public Provedor(String nome, ArrayList<PI> pis, double custo)
    {
        if(nome == null || nome.isEmpty())
            throw new IllegalArgumentException("O nome do Provedor deve ser válido!");
        this.nome = nome.toLowerCase().replaceAll(" ", "").replaceAll("\t", "").replaceAll("\n", "");
        
        if(pis == null || pis.isEmpty())
            throw new IllegalArgumentException("Entrada vazia!"); 
        
        for(int i = 0; i < pis.size(); i++)        
            if(!this.inserePI(pis.get(i)))
                throw new IllegalArgumentException("O PI " + pis.get(i).getNome() + " nao pode ser inserido!");     
        
        this.removePI("custo");
        
        if(custo > 0)
            this.custo = custo;
    }    
    /**
     * 
     * @param nome
     * @param custo 
     */
    public Provedor(String nome, double custo) 
    {
        if(nome == null || nome.isEmpty())
            throw new IllegalArgumentException("O nome do Provedor deve ser válido!");
        this.nome = nome.toLowerCase().replaceAll(" ", "").replaceAll("\t", "").replaceAll("\n", "");
        
        if(custo > 0)
            this.custo = custo;
    }
    /**
     * 
     * @param nome
     * @param pis 
     */
    public Provedor(String nome, ArrayList<PI> pis)
    {
        if(nome == null || nome.isEmpty())
            throw new IllegalArgumentException("O nome do Provedor deve ser válido!");
        this.nome = nome.toLowerCase().replaceAll(" ", "").replaceAll("\t", "").replaceAll("\n", "");
        
        if(pis == null || pis.isEmpty())
            throw new IllegalArgumentException("Entrada vazia!"); 
        
        for(int i = 0; i < pis.size(); i++)        
            if(!this.inserePI(pis.get(i)))
                throw new IllegalArgumentException("O PI " + pis.get(i).getNome() + " nao pode ser inserido!");               
        
        this.removePI("custo");
    }
    /**
     * 
     * @param nome 
     */
    public Provedor(String nome) 
    {
        if(nome == null || nome.isEmpty())
            throw new IllegalArgumentException("O nome do Provedor deve ser válido!");
        this.nome = nome.toLowerCase().replaceAll(" ", "").replaceAll("\t", "").replaceAll("\n", "");
    }
    // # # # # # # # # # # # # # # # # # # # # # # # #
    
    // GETTERS
    /** @return O nome do provedor. */
    public String getNome()
    {
        return this.nome;
    }
    /** @return O custo do provedor. */
    public double getCusto()
    {
        return custo;
    }
    
    /**
     * O PI pertencente a esse provedor na posição passada.
     * @param pos A posição do PI.
     * @return O PI desse provedor.
     */
    public PI getPiPos(int pos)
    {
        return this.pis.get(pos);
    }
    /** @return O número total de PIs desse provedor. */
    public final int getNumPis() 
    {
        return this.pis.size();
    }
    /**
     * Obtem o PI pelo seu nome.
     * @param nomePI Nome do PI.
     * @return O PI de nome passado.
     */
    public PI getPI(String nomePI) 
    {
        nomePI = nomePI.toLowerCase().replaceAll(" ", "").replaceAll("\t", "");
        for(int i = 0; i < this.pis.size(); i++)
            if(this.pis.get(i).getNome().equals(nomePI))
                return this.pis.get(i);
        
        return null;
    }
    // SETTERS
    /**
     * Modifica o nome do PI, não pode ser vazio ou nulo.
     * @param nomePI Novo nome do PI.
     */
    public void setNome(String nomePI) 
    {
        if(nomePI != null && ! nomePI.isEmpty())
            this.nome = nomePI.toLowerCase().replaceAll(" ", "").replaceAll("\t", "").replaceAll("\n", "");
    }  
    /**
     * Modifica o custo do PI, não pode ser negativo.
     * @param custo Novo custo do PI.
     */
    public void setCusto(double custo)
    {
        if(custo >= 0)
            this.custo = custo;        
    }    
        
    /**
     * Verifica se o PI de nome passado existe no provedor.
     * @param nomePI Nome do PI.
     * @return "true" se ele existe, "false", caso contrário.
     */
    public final boolean piExiste(String nomePI)
    {
        if(nomePI == null || nomePI.isEmpty())
            throw new IllegalArgumentException("Nome de PI invalido!");
        
        String nome = nomePI.toLowerCase().replaceAll(" ", "").replaceAll("\t", "");
        for(int i = 0; i < this.pis.size(); i++)
            if(this.pis.get(i).getNome().equals(nome))
                return true;
        
        return false;
    }
    /**
     * Insere um novo PI no provedor. Não repete nomes.  
     * @param pi Pi a ser inserido.
     * @return "true" se a inserção foi bem sucedida, "false", caso contrário.
     */
    public final boolean inserePI(PI pi)
    {
        if(pi == null) // Inválido
            throw new IllegalArgumentException("PI inválido!");
        // Não repete nomes
        if(piExiste(pi.getNome())) 
            return false;
        // O custo é um PI especial
        /*if(pi.getNome().equals("custo"))
        {
            this.setCusto(pi.getValor());
            return true;
        } */       
        return (this.pis.add(pi));        
    } 
    /**
     * Remove um PI do provedor.
     * @param nomePI Nome do PI.
     * @return "true" se a remoção foi bem sucedida, "false", caso contrário.
     */
    public final boolean removePI(String nomePI)
    {
        if(nomePI == null || nomePI.isEmpty() || !piExiste(nomePI))
            return false; 
        
        String nome = nomePI.toLowerCase().replaceAll(" ", "").replaceAll("\t", "");        
        // Procura o PI para remover
        for(int i = 0; i < this.pis.size(); i++)
        {
            if(this.pis.get(i).getNome().equals(nome))
            {   // PI encontrado
                this.pis.remove(i);
                return true;
            }
        }     
        // PI nao encontrado
        return false;
    }
    /**
     * Remove todos os PIs do provedor.
     */
    public final void removeTudo()
    {
        this.pis.clear();
        this.custo = 0.00;
    } 
    
    @Override
    public String toString() 
    {
        String ret = "Provedor: " + nome + "\n";
        if(this.pis.isEmpty())  
            ret += "Vazio!";            
        else            
            for(int i = 0; i < this.pis.size(); i++)
                ret += this.pis.get(i);
        
        return (ret + "Custo: " + custo + "\n");
    }     
    @Override
    public boolean equals(Object obj)
    {
        if(obj == null || this.getClass() != obj.getClass())  return false;
        if(this == obj)  return true;
        
        final Provedor other = (Provedor) obj;
        return Objects.equals(this.nome, other.nome);
    }
    @Override
    public int hashCode() 
    {
        int hash = 7;
        hash = 17 * hash + Objects.hashCode(this.nome);
        return hash;
    }  
}