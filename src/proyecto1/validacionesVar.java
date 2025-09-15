package proyecto1;
import java.io.*;
import java.util.*;
/**
 *
 * @author Dell
 */
public class validacionesVar {

  //Lista de palabras reservadas de Pascal
  private static final Set < String > reservadas = new HashSet < > (Arrays.asList(
    "ABSOLUTE", "DOWNTO", "BEGIN", "DESTRUCTOR", "MOD", "AND", "ELSE", "CASE", "EXTERNAL", "NOT",
    "ARRAY", "END", "CONST", "DIV", "PACKED", "ASM", "FILE", "CONSTRUCTOR", "DO", "PROCEDURE", "FOR",
    "FORWARD", "FUNCTION", "GOTO", "RECORD", "IF", "IN", "OR", "PRIVATE", "UNTIL", "PROGRAM", "REPEAT",
    "STRING", "THEN", "VAR", "WHILE", "XOR", "WITH", "TYPE", "OF", "USES", "SET", "OBJECT", "TO"
  ));

  //Método para las validaciones de los indicadores
  public static void validarVar(String lineaTrim, int numeroLinea, PrintWriter salida, boolean esperandoUses,
    boolean beginEncontrado, boolean constEncontrado, Set < String > identificadores
  ) {
    //Verificar que los indicadores estén en la posición correcta
    if (esperandoUses || beginEncontrado || !constEncontrado) {
      salida.printf("Error 211. Línea %04d. 'var' debe aparecer después de 'const' y antes de 'begin'%n", numeroLinea);
    }

    String declaracion = lineaTrim.substring(3).trim(); //Quitar "var"
    String[] partes = declaracion.split(",");

    try {
      //Recorrer cada identificador después de var y verificar que sea correcto
      for (String parte: partes) {
        parte = parte.trim();
        String identificador = parte.split(":")[0].trim();
        identificadores.add(identificador);

        if (identificador.isEmpty()) {
          salida.printf("Error 3333. Línea %04d. Debe existir un nombre para la variable.%n", numeroLinea);
        }

        if (reservadas.contains(identificador.toUpperCase())) {
          salida.printf("Error 226. Línea %04d. Se está utilizando una palabra reservada para declarar un identificador%n", numeroLinea);
        }

        if (lineaTrim.contains(":")) {
          String espacioDespues = lineaTrim.substring(lineaTrim.indexOf(":") + 1);
          String espacioAntes = lineaTrim.substring(lineaTrim.indexOf(":") - 1);
          if (!espacioDespues.startsWith(" ")) {
            salida.printf("Error. Línea %04d. Debe haber un espacio después de los dos puntos en la declaración de la variable.%n", numeroLinea);
          }
          if(!espacioAntes.startsWith(" ")){
               salida.printf("Error. Línea %04d. Debe haber un espacio antes de los dos puntos en la declaración de la variable.%n", numeroLinea);
          }
        }
        //Comprobar si empieza por una letra 
        if (!Character.isLetter(identificador.charAt(0))) {
          salida.printf("Error. Línea %04d. El nombre de la variable '%s' debe comenzar con una letra.%n", numeroLinea, identificador);
        } else if (!identificador.matches("^[a-zA-Z][a-zA-Z_]*$")) {
          salida.printf("Error. Línea %04d. El identificador '%s' es inválido. Usar solo letras y guiones bajos.%n", numeroLinea, identificador);
        }
        //Tomar lo que esta después de los 2 puntos
        String despuesDosPuntos = lineaTrim.substring(lineaTrim.indexOf(":") + 1).trim();

        //Quitar los 2 puntos para validar solo el tipo
        if (despuesDosPuntos.endsWith(";")) {
          despuesDosPuntos = despuesDosPuntos.substring(0, despuesDosPuntos.length() - 1).trim();
        }

        //Quedarse solo con lo que esta después de 'of' si esta después de los 2 puntos
        if (despuesDosPuntos.toLowerCase().contains("of")) {
          despuesDosPuntos = despuesDosPuntos.substring(despuesDosPuntos.toLowerCase().lastIndexOf("of") + 2).trim();
        }

        //Validar que el tipo sea integer, string o word
        if (!(despuesDosPuntos.equalsIgnoreCase("integer") ||
            despuesDosPuntos.equalsIgnoreCase("string") ||
            despuesDosPuntos.equalsIgnoreCase("word"))) {
          salida.printf("Error. Línea %04d. Tipo de variable inválido: %s%n", numeroLinea, despuesDosPuntos);
        } else if (!lineaTrim.endsWith(";")) {
          salida.printf("Error. Línea %04d. La declaración del var debe terminar con punto y coma.%n", numeroLinea, despuesDosPuntos);
        }
      }
    } catch (Exception e) {
      salida.printf("Error inesperado en validación de 'var' en la línea %04d: %s%n", numeroLinea, e.getMessage());
    }
  }
}