package proyecto1;

import java.io.*;
/**
 *
 * @author Dell
 */
public class validacionesIniciales {

  public static void validarProgram(String lineaTrim, String linea, int numeroLinea, String nombreArchivo, PrintWriter salida,
    boolean programOriginal, boolean esperandoUses
  ) {
    try {
      if (!programOriginal) {
        //Verificar que no haya contenido antes de program
        if (lineaTrim.isEmpty() || lineaTrim.startsWith("//") || lineaTrim.startsWith("{")) {
          salida.printf("Error 204. Línea %04d. No pueden haber líneas con o sin contenido antes de 'program'%n", numeroLinea);
        }

        //Verificar que el archivo .pas inicie con program
        if (!lineaTrim.startsWith("program")) {
          salida.printf("Error 204. Línea %04d. El código debe comenzar con 'program'%n", numeroLinea);
        }

        //Comprobar que termine en punto y coma
        if (!lineaTrim.endsWith(";")) {
          salida.printf("Error 202. Línea %04d. Falta punto y coma al final%n", numeroLinea);
        }

        //Comprobar el nombre después de 'program'
        String nombrePas = lineaTrim.substring(7).replace(";", "").trim();
        if (!nombrePas.equalsIgnoreCase(nombreArchivo)) {
          salida.printf("Error 201. Línea %04d. El nombre '%s' no coincide con el archivo '%s'%n",
            numeroLinea, nombrePas, nombreArchivo);
        }
      }
    } catch (Exception e) {
      salida.printf("Error inesperado en validación de 'var' en la línea %04d: %s%n", numeroLinea, e.getMessage());
    }
  }

  public static void validarUses(String lineaTrim, int numeroLinea, PrintWriter salida, boolean esperandoUses
  ) {
    if (esperandoUses) {
      try {
        if (lineaTrim.isEmpty() || lineaTrim.startsWith("//") || lineaTrim.startsWith("{")) {
          salida.printf("Error 205. Línea %04d. No puede haber nada(incluyendo espacios vacios) entre 'program' y 'uses'%n", numeroLinea);
        }

        //Verificar que exista uses después de program
        if (!lineaTrim.startsWith("uses")) {
          salida.printf("Error 206. Línea %04d. Debe aparecer 'uses' después de 'program'%n", numeroLinea);
        }

        if (!lineaTrim.endsWith(";")) {
          salida.printf("Error 207. Línea %04d. La línea 'uses' debe terminar en punto y coma%n", numeroLinea);
        } else {
          //Verificar que exista un comando
          String comando = lineaTrim.substring(4).replace(";", "").trim();
          if (comando.isEmpty()) {
            salida.printf("Error 299. Línea %04d. Debe existir un comando después de uses%n", numeroLinea);
          }
        }
      } catch (Exception e) {
        salida.printf("Error inesperado en validación de 'var' en la línea %04d: %s%n", numeroLinea, e.getMessage());
      }
    }
  }

  public static void programRepetido(String lineaTrim, int numeroLinea, PrintWriter salida) {
    //Revisar las líneas para comprobar que no se repite program 
    for (String palabra: lineaTrim.split("[^a-zA-Z0-9_]+")) {
      if (palabra.equalsIgnoreCase("program")) {
        salida.printf("Error 203. Línea %04d. 'program' está repetido%n", numeroLinea);
      }
    }
  }
}