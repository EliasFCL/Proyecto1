package proyecto1;
import java.io.*;
import java.util.*;
/**
 *
 * @author Dell
 */
public class validacionesVar {
  //Método para las validaciones de los indicadores
  public static void validarVar(String lineaTrim, int numeroLinea, PrintWriter salida, boolean esperandoUses,
    boolean beginEncontrado, boolean constEncontrado, Set < String > identificadores) {
    //Verificar que los indicadores estén en la posición correcta
    if (esperandoUses || beginEncontrado || !constEncontrado) {
      salida.printf("Error 020. Línea %04d. 'var' debe aparecer después de 'const' y antes de 'begin'%n", numeroLinea);
    }

    String declaracion = lineaTrim.substring(3).trim(); //Quitar "var"

    try {
      //Obtener identificador (lo que está antes de los 2 puntos)
      String identificador = declaracion.split(":")[0].trim(); //El identificador sera lo que esta antes de ":"
      identificadores.add(identificador);//Se añade el identificador a la lista

      //Verificar que la declaracion contenga los 2 puntos y los espacios
      if (lineaTrim.contains(":")) {
        String espacioDespues = lineaTrim.substring(lineaTrim.indexOf(":") + 1);
        String espacioAntes = lineaTrim.substring(lineaTrim.indexOf(":") - 1);
        if (!espacioDespues.startsWith(" ")) {
          salida.printf("Error 023. Línea %04d. Debe haber un espacio después de los dos puntos en la declaración de la variable.%n", numeroLinea);
        }
        if (!espacioAntes.startsWith(" ")) {
          salida.printf("Error 024. Línea %04d. Debe haber un espacio antes de los dos puntos en la declaración de la variable.%n", numeroLinea);
        }
      } else {
        salida.printf("Error 025. Línea %04d. La declaración debe tener un ':' después del nombre.%n", numeroLinea);
      }

      //Validaciones del identificador
        if (!Character.isLetter(identificador.charAt(0))) {
          salida.printf("Error 034. Línea %04d. El nombre de la variable '%s' debe comenzar con una letra.%n", numeroLinea, identificador);
        } else if (!identificador.matches("^[a-zA-Z][a-zA-Z_]*$")) {
          salida.printf("Error 035. Línea %04d. El identificador '%s' es inválido. Usar solo letras y guiones bajos.%n", numeroLinea, identificador);
        } else if (metodos.tipos.Reservada(identificador) == true) {
          salida.printf("Error 036. Línea %04d. Se está usando una palabra reservada para la variable.%n", numeroLinea, identificador);
        }

        if (identificador.isEmpty()) {
          salida.printf("Error 037. Línea %04d. El nombre de la constante no puede ser nulo%n", numeroLinea);
        }

      //Comprobar lo que está después de los 2 puntos
      String tipoVar = lineaTrim.substring(lineaTrim.indexOf(":") + 1).trim();
      if (lineaTrim.contains(":")) {
        
        //Quitar el punto y coma final
        if (tipoVar.endsWith(";")) {
          tipoVar = tipoVar.replace(";","").trim();
        }else{
            salida.printf("Error 029. Línea %04d. La declaración del var debe terminar con punto y coma.%n", numeroLinea, tipoVar);
        }

        //Validar que el tipo sea integer, string o word
        if (!(tipoVar.equals("integer") || tipoVar.equals("string") || tipoVar.equals("word"))) {
          salida.printf("Error 028. Línea %04d. Tipo de variable inválido: %s%n", numeroLinea, tipoVar);
        }
      }
    } catch (Exception e) {
      salida.printf("Error inesperado en validación de 'var' en la línea %04d: %s%n", numeroLinea, e.getMessage());
    }
  }
}