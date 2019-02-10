/*
 * UDESC - Universidade do Estado de Santa Catarina
 * Aluno: Lucas Borges de Moraes
 * Curso: Mestrado em Computação Aplicada - MCA 
 * Orientadores: Adriano Fiorese e Rafael Parpinelli
 */
package modelos;

import java.util.Objects;
import static modelos.Constantes.*;

public class PI_Num extends PI
{
    
    /** Tipo do PI utilizado. Pode ser: NB, HB ou LB. Padrao: NB. */
    private int tipo = NB;
    /** Valor numérico associado a cada PI utilizado. */
    private double valor = 0.0;
    /** Tolerância numérica atribuída a esse PI (sempre positiva). */
    private double tol = 0.0; // Opcional
           
    // # # # # # # # # Construtores # # # # # # # #
    /**
     * 
     * @param nome
     * @param tipo
     * @param valor
     * @param tol
     * @param peso
     * @param essencial 
     */
    public PI_Num(String nome, int tipo, double valor, double tol, double peso, boolean essencial) 
    {
        this.TYPE = NUMERICO;
        if(nome == null || nome.isEmpty())
            throw new IllegalArgumentException("O nome do PI deve ser válido!");
        this.nome = nome.toLowerCase().replaceAll(" ", "").replaceAll("\t", "").replaceAll("\n", "");
        if(!tipoValido(tipo))
            throw new IllegalArgumentException("Tipo inválido de PI! Tipos possíveis: NB, HB e LB");
        this.tipo = tipo;
        if(peso <= 0)
            throw new IllegalArgumentException("O peso do PI deve ser maior que zero!");
        this.peso = peso;
        this.valor = valor;
        this.tol = Math.abs(tol);
        this.essencial = essencial;
    }
    /**
     * 
     * @param nome
     * @param tipo
     * @param valor
     * @param peso
     * @param grupo 
     */
    public PI_Num(String nome, int tipo, double valor, double peso, int grupo) 
    {
        this.TYPE = NUMERICO;
        if(nome == null || nome.isEmpty())
            throw new IllegalArgumentException("O nome do PI deve ser válido!");
        this.nome = nome.toLowerCase().replaceAll(" ", "").replaceAll("\t", "").replaceAll("\n", "");
        if(!tipoValido(tipo))
            throw new IllegalArgumentException("Tipo inválido de PI! Tipos possíveis: NB, HB e LB");
        this.tipo = tipo;
        if(peso <= 0)
            throw new IllegalArgumentException("O peso do PI deve ser maior que zero!");
        this.peso = peso;
        this.valor = valor;
        if(grupo > 0)
            this.grupo = grupo;
    }        
    /**
     * 
     * @param nome
     * @param tipo
     * @param grupo 
     */
    public PI_Num(String nome, int tipo, int grupo) 
    {
        this.TYPE = NUMERICO;
        if(nome == null || nome.isEmpty())
            throw new IllegalArgumentException("O nome do PI deve ser válido!");
        this.nome = nome.toLowerCase().replaceAll(" ", "").replaceAll("\t", "").replaceAll("\n", "");
        if(!tipoValido(tipo))
            throw new IllegalArgumentException("Tipo inválido de PI! Tipos possíveis: NB, HB e LB");
        this.tipo = tipo;
        if(grupo > 0)
            this.grupo = grupo;
    }
    /**
     * 
     * @param nome
     * @param grupo 
     */
    public PI_Num(String nome, int grupo) 
    {
        this.TYPE = NUMERICO;
        if(nome == null || nome.isEmpty())
            throw new IllegalArgumentException("O nome do PI deve ser válido!");
        this.nome = nome.toLowerCase().replaceAll(" ", "").replaceAll("\t", "").replaceAll("\n", "");
        if(grupo > 0)
            this.grupo = grupo;
    }    
    // # # # # # # # # # # # # # # # # # # # # # # # #
    
    // GETTERS        
    /** @return O tipo do PI. */
    @Override
    public int getTipo()
    {
        return tipo;
    }
    /** @return O valor do PI. */    
    @Override
    public String getValor()
    {
        return "" + valor;
    }
    /** @return A tolerancia desse PI. */
    @Override
    public double getTol()
    {
        return tol;
    }
    
    
    // SETTERS    
    /**
     * Modifica o tipo do PI. Pode ser: NB, HB ou LB.
     * @param tipo Novo tipo para o PI.
     */
    @Override
    public void setTipo(int tipo) 
    {
        if(tipoValido(tipo))
            this.tipo = tipo;
    }
    /**
     * Modifica o valor do PI.
     * @param valor Novo valor para o PI.
     */
    public void setValor(double valor)
    {
        this.valor = valor;
    }
    /**
     * Modifica o valor da tolerancia do PI real.
     * @param tol Novo valor de tolerancia (positivo) para o PI.
     */
    @Override
    public void setTol(double tol)
    {
        this.tol = Math.abs(tol);
    }
    
    /**
     * Verifica se o tipo é valido para o PI quantitativo.
     * @param tipo O tipo.
     * @return "true" caso seja valido; "false", caso contrario.
     */    
    public final boolean tipoValido(int tipo)
    {
        return (tipo == DB || tipo == NB || tipo == HB || tipo == LB);            
    }
    /**
     * Verifica se este PI atende um valor "z", desejado pelo usuario.    
     * @param z O valor desejado pelo usuário na requisição.     
     * @return "true" caso atenda; "false", caso contrario.
     */    
    public boolean atende(double z)
    {
             if(tipo == HB && ( valor >= (z - tol)) )  return true;  
        else if(tipo == LB && ( valor <= (z + tol)) )  return true;  
        else if(tipo == NB && ( valor >= (z - tol) && 
                                valor <= (z + tol)) )  return true; 
        
        return false;        
    }
    /**
     * Verifica se este PI atende um valor "z", desejado pelo usuario.    
     * @param z O valor desejado pelo usuário na requisição.     
     * @param tol O valor da tolerancia maxima para com o valor desejado z.
     * @return "true" caso atenda; "false", caso contrario.
     */    
    public boolean atende(double z, double tol)
    {
        tol = Math.abs(tol); // Sempre positivo
             if(tipo == HB && ( valor >= (z - tol)) )  return true;  
        else if(tipo == LB && ( valor <= (z + tol)) )  return true;  
        else if(tipo == NB && ( valor >= (z - tol) && 
                                valor <= (z + tol)) )  return true; 
        
        return false;        
    }
    /**
     * Verifica se o PI "x" atende um valor "z", desejado pelo usuario.
     * @param x O valor do PI no provedor do banco de dados (O QUE SE TEM).
     * @param z O valor desejado pelo usuário na requisição (O QUE SE QUER).
     * @param tipo O tipo de comportamento desse PI.
     * @param tol O valor da tolerancia maxima para com o valor desejado z.
     * @return "true" caso atenda; "false", caso contrario.
     */
    public static boolean atende(double x, double z, int tipo, double tol)
    {
        tol = Math.abs(tol); // Sempre positivo
             if(tipo == HB && ( x >= (z - tol)) )  return true;  
        else if(tipo == LB && ( x <= (z + tol)) )  return true;  
        else if(tipo == NB && ( x >= (z - tol) && 
                                x <= (z + tol)) )  return true; 
        
        return false;
    }
    
    @Override
    public String toString() 
    { // nome peso grupo essencial
        return "PI NUMERICO " + ( grupo > 0 ? ("(Gr " + grupo + ")") : "" ) +
                ": " + nome + " (" + imprimeTipo(tipo) + ") = " + valor + 
                (tol > 0 ? (". Tol = " + tol) : "") + 
                (peso > 0 ? (". Peso = " + peso) : "") + 
                (essencial ? ". Essencial" : "") + "\n";
    }    
    @Override
    public boolean equals(Object obj) 
    {
        if(obj == null || this.getClass() != obj.getClass())  return false;
        if(this == obj)  return true;
        
        final PI other = (PI) obj;
        return Objects.equals(this.nome, other.nome);
    }
    @Override
    public int hashCode() 
    {
        int hash = 5;
        hash = 53 * hash + Objects.hashCode(this.nome);
        return hash;
    }
}