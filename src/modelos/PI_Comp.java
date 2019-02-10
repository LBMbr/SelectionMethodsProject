/*
 * UDESC - Universidade do Estado de Santa Catarina
 * Aluno: Lucas Borges de Moraes
 * Curso: Mestrado em Computação Aplicada - MCA 
 * Orientadores: Adriano Fiorese e Rafael Parpinelli
 */
package modelos;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import static modelos.Constantes.*;

public class PI_Comp extends PI
{
    /** Valor do PI categórico composto é uma ou várias categorias (Strings alfanuméricas). */
    private final Set<String> valores = new HashSet();
    /** São as categorias que esse indicador pode assumir. */
    private final Set<String> categorias = new HashSet();
    
    // # # # # # # # # CONSTRUTORES # # # # # # # #
    /**
     * 
     * @param nome
     * @param categorias 
     */
    public PI_Comp(String nome, String[] categorias) 
    {
        this.TYPE = CAT_COMP;
        if(nome == null || nome.isEmpty())
            throw new IllegalArgumentException("O nome do PI deve ser válido!");
        this.nome = nome.toLowerCase().replaceAll(" ", "").replaceAll("\t", "").replaceAll("\n", "");
        
        if(categorias == null || categorias.length == 0)
            throw new IllegalArgumentException("Quantidade de categorias insuficientes para o PI " + nome + "!");
        for(int i = 0; i < categorias.length; i++)
        {
            if(categorias[i] == null || categorias[i].isEmpty())
                throw new IllegalArgumentException("A categoria "  + (i+1) + " do PI " + nome + " é inválida!");
            categorias[i] = categorias[i].toLowerCase().replaceAll(" ", "").replaceAll("\t", "").replaceAll("\n", "");
            if(!ehStringAlfaNumerica(categorias[i]))
                throw new IllegalArgumentException("A categoria "  + (i+1) + " do PI " + nome + " deve ser alfanumerica!");
            this.categorias.add(categorias[i]);
        }
    }    
    /**
     * 
     * @param nome
     * @param valores
     * @param peso
     * @param essencial    
     */
    public PI_Comp(String nome, String[] valores, double peso, boolean essencial) 
    {
        this.TYPE = CAT_COMP;
        if(nome == null || nome.isEmpty())
            throw new IllegalArgumentException("O nome do PI deve ser válido!");
        this.nome = nome.toLowerCase().replaceAll(" ", "").replaceAll("\t", "").replaceAll("\n", "");
        
        if(valores == null || valores.length == 0)
            throw new IllegalArgumentException("Quantidade de categorias insuficientes para o PI " + nome + "!");
        for(int i = 0; i < valores.length; i++)
        {
            if(valores[i] == null || valores[i].isEmpty())
                throw new IllegalArgumentException("O valor "  + (i+1) + " do PI " + nome + " é inválido!");
            valores[i] = valores[i].toLowerCase().replaceAll(" ", "").replaceAll("\t", "").replaceAll("\n", "");
            if(!ehStringAlfaNumerica(valores[i]))
                throw new IllegalArgumentException("O valor "  + (i+1) + " do PI " + nome + " deve ser alfanumerico!");
            
            this.valores.add(valores[i]);
        }
        
        if(peso <= 0)
            throw new IllegalArgumentException("O peso do PI deve ser maior que zero!");
        this.peso = peso;
        this.essencial = essencial;
    }
    /**
     * 
     * @param nome
     * @param categorias
     * @param valores 
     */
    public PI_Comp(String nome, String[] categorias, String[] valores) 
    {
        this.TYPE = CAT_COMP;
        if(nome == null || nome.isEmpty())
            throw new IllegalArgumentException("O nome do PI deve ser válido!");
        this.nome = nome.toLowerCase().replaceAll(" ", "").replaceAll("\t", "").replaceAll("\n", ""); 
                     
        if(categorias == null || categorias.length == 0)
            throw new IllegalArgumentException("Quantidade de categorias insuficientes para o PI " + nome + "!");
        for(int i = 0; i < categorias.length; i++)
        {
            if(categorias[i] == null || categorias[i].isEmpty())
                throw new IllegalArgumentException("A categoria "  + (i+1) + " do PI " + nome + " é inválida!");
            categorias[i] = categorias[i].toLowerCase().replaceAll(" ", "").replaceAll("\t", "").replaceAll("\n", "");
            if(!ehStringAlfaNumerica(categorias[i]))
                throw new IllegalArgumentException("A categoria "  + (i+1) + " do PI " + nome + " deve ser alfanumerica!");
            this.categorias.add(categorias[i]);
        }
        if(valores == null || valores.length == 0)
            throw new IllegalArgumentException("Quantidade de categorias insuficientes para o PI " + nome + "!");
        for(int i = 0; i < valores.length; i++)
        {
            if(valores[i] == null || valores[i].isEmpty())
                throw new IllegalArgumentException("O valor "  + (i+1) + " do PI " + nome + " é inválido!");
            valores[i] = valores[i].toLowerCase().replaceAll(" ", "").replaceAll("\t", "").replaceAll("\n", "");
            if(!ehStringAlfaNumerica(valores[i]))
                throw new IllegalArgumentException("O valor "  + (i+1) + " do PI " + nome + " deve ser alfanumerico!");
            
            this.valores.add(valores[i]);
        }                      
    }
    /**
     * 
     * @param nome
     * @param grupo
     * @param categorias 
     */
    public PI_Comp(String nome, int grupo, String[] categorias) 
    {
        this.TYPE = CAT_COMP;
        if(nome == null || nome.isEmpty())
            throw new IllegalArgumentException("O nome do PI deve ser válido!");
        this.nome = nome.toLowerCase().replaceAll(" ", "").replaceAll("\t", "").replaceAll("\n", "");
        
        if(grupo > 0)
            this.grupo = grupo;
        
        if(categorias == null || categorias.length == 0)
            throw new IllegalArgumentException("Quantidade de categorias insuficientes para o PI " + nome + "!");
        for(int i = 0; i < categorias.length; i++)
        {
            if(categorias[i] == null || categorias[i].isEmpty())
                throw new IllegalArgumentException("A categoria "  + (i+1) + " do PI " + nome + " é inválida!");
            categorias[i] = categorias[i].toLowerCase().replaceAll(" ", "").replaceAll("\t", "").replaceAll("\n", "");
            if(!ehStringAlfaNumerica(categorias[i]))
                throw new IllegalArgumentException("A categoria "  + (i+1) + " do PI " + nome + " deve ser alfanumerica!");
            this.categorias.add(categorias[i]);
        }
    }
    
    /**
     * 
     * @param categorias
     * @return 
     */
    public boolean insereNovasCategorias(String[] categorias)
    {
        if(categorias != null)
        {
            this.categorias.clear();
            for(int i = 0; i < categorias.length; i++)
            {
                if(categorias[i] == null || categorias[i].isEmpty())
                    throw new IllegalArgumentException("A categoria "  + (i+1) + " do PI " + nome + " é inválida!");
                categorias[i] = categorias[i].toLowerCase().replaceAll(" ", "").replaceAll("\t", "").replaceAll("\n", "");
                if(!ehStringAlfaNumerica(categorias[i]))
                    throw new IllegalArgumentException("A categoria "  + (i+1) + " do PI " + nome + " deve ser alfanumerica!");
                this.categorias.add(categorias[i]);
            }
            return true;
        }
        return false;
    }
    /**
     * 
     * @param categorias
     * @return 
     */
    public boolean insereNovasCategorias(Categoria[] categorias)
    {
        if(categorias != null)
        {
            this.categorias.clear();
            for(int i = 0; i < categorias.length; i++)
            {
                if(categorias[i] == null)
                    throw new IllegalArgumentException("A categoria "  + (i+1) + " do PI " + nome + " é inválida!");
                
                this.categorias.add(categorias[i].getNome());
            }
            return true;
        }
        return false;
    }
    
    // # # # # # # # # # FIM # # # # # # # # #
    // GETTERS
    /** @return Os valores do PI categorico composto. */
    public Set<String> getValores() 
    {
        return valores;
    }
    /** @return Os valores do PI categorico composto em forma de string unica, separados por virgula. */
    @Override
    public String getValor() 
    {
        String ret = ""; 
        for(Iterator<String> iter = this.valores.iterator(); iter.hasNext(); )
            ret += iter.next() + ",";        
        return ret.substring(0, ret.length() - 1); // Remove a ultima virgula
    }
    /** @return O numero de categorias cadastradsa nesse PI categorico composto. */
    public int getNumCat()
    {
        return this.categorias.size();
    }  
    /** @return As categorias cadastradas para esse PI categorico composto. */
    @Override
    public Categoria[] getCats()
    {
        int i = 0;
        Categoria[] cats = new Categoria[categorias.size()];
        if(!categorias.isEmpty())        
            for(Iterator<String> iter = this.categorias.iterator(); iter.hasNext(); )
            {
                cats[i] = new Categoria(iter.next());
                i++;
            }
        
        return cats;
    }
    /** @return As categorias cadastradas para esse PI categorico em formato unico de string. */
    public final String getStringCats()
    {
        Categoria[] cats = this.getCats();
        String ret = "Categorias: ";
        for(int i = 0; i < cats.length; i++)
            ret += cats[i].toString();
        return ret;
    }
    // SETTER
    /**
     * Modifica o valor do PI categorico composto.
     * @param valores Novos valores para o PI composto.
     */
    public void setValor(String[] valores) 
    {
        if(valoresValidos(valores))
        {
            this.valores.clear();
            for(int i = 0; i < valores.length; i++)
                this.valores.add(valores[i]);
        }            
    }
    /**
     * Verifica se os valores passado são valido para o PI categorico composto 
     * cujas categorias possíveis já foram pré-estabelecidas.
     * @param valores O valor.
     * @return "true" caso seja valido; "false", caso contrario.
     */
    public final boolean valoresValidos(String[] valores)
    {
        if(valores == null && valores.length == 0)
            return false;
        
        for(int i = 0; i < valores.length; i++)
        {
            if(valores[i] == null || valores[i].isEmpty())
                return false;
            valores[i] = valores[i].toLowerCase().replaceAll(" ", "").replaceAll("\t", "").replaceAll("\n", "");
            if(!ehStringAlfaNumerica(valores[i]) || !this.categorias.contains(valores[i]))
                return false;             
        }        
        return true;             
    }
    /**
     * Verifica se este PI categorico atende um os valores "y", desejados pelo usuario.    
     * @param y O valor desejado pelo usuário na requisição.     
     * @return "true" caso atenda; "false", caso contrario.
     */
    public final boolean atende(String[] y)
    {                    
        for(int i = 0; i < y.length; i++)        
            if(!this.valores.contains(y[i].toLowerCase().replaceAll(" ", "").replaceAll("\t", "").replaceAll("\n", "")))            
                return false;
             
        return true;        
    }
    /** Imprime as categorias cadastradas para esse PI categorico composto. */
    @Override
    public void imprimeCats()
    {
        if(!this.categorias.isEmpty())
        {
            for(Iterator<String> iter = this.categorias.iterator(); iter.hasNext(); )
                System.out.print(iter.next() + " ");
            System.out.println();
        }
        else
            System.out.println("Vazio!");
    }
    
    @Override
    public String toString()
    {
        return "PI CATEGORICO COMPOSTO " + (grupo > 0 ? ("(Gr " + grupo + ")") : "") +
                ": " + nome + " (NB) = " + valores +
                (peso > 0 ? (". Peso = " + peso) : "" ) +
                (essencial ? ". Essencial" : "") + ". " + getStringCats() + "\n";
    }
}
