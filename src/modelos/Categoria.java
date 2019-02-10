/*
 * UDESC - Universidade do Estado de Santa Catarina
 * Aluno: Lucas Borges de Moraes
 * Curso: Mestrado em Computação Aplicada - MCA 
 * Orientador: Adriano Fiorese
 */
package modelos;

import java.util.Objects;
import static modelos.Constantes.*;

public class Categoria
{
    private int nivel = 0;
    private String nome;
    
    /**
     * 
     * @param nivel
     * @param nome 
     */
    public Categoria(int nivel, String nome)
    {
        if(nivel < 0 || nome == null || nome.isEmpty() || !ehStringAlfaNumerica(nome))
            throw new IllegalArgumentException("Nivel ou nome de categoria invalido!");
        this.nivel = nivel;
        this.nome = nome.toLowerCase().replaceAll(" ", "").replaceAll("\t", "").replaceAll("\n", "");
    }
    /**
     * 
     * @param nome 
     */
    public Categoria(String nome)
    {
        if(nome == null || nome.isEmpty() || !ehStringAlfaNumerica(nome))
            throw new IllegalArgumentException("Nome de categoria invalido!");
        this.nome = nome.toLowerCase().replaceAll(" ", "").replaceAll("\t", "").replaceAll("\n", "");
    }    
    // GETTERS
    public int getNivel()
    {
        return nivel;
    }
    public String getNome()
    {
        return nome;
    }
    // SETTERS
    public void setNivel(int nivel)
    {
        if(nivel >= 0)
            this.nivel = nivel;
    }
    public void setNome(String nome)
    {
        if(nome != null && !nome.isEmpty() && ehStringAlfaNumerica(nome))
            this.nome = nome.toLowerCase().replaceAll(" ", "").replaceAll("\t", "").replaceAll("\n", "");
    }

    @Override
    public String toString()
    {
        return "(" + nivel + ", " + nome + ")";
    }
    @Override
    public boolean equals(Object obj)
    {
        if(obj == null || this.getClass() != obj.getClass())  return false;
        if(this == obj)  return true;
        
        final Categoria other = (Categoria) obj;
        return (Objects.equals(this.nome, other.nome));
    }
    @Override
    public int hashCode()
    {
        int hash = 5;
        hash = 67 * hash + Objects.hashCode(this.nome);
        return hash;
    }      
}
