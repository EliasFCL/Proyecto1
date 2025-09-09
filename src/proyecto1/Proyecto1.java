//Leer desde el cmd: https://www.delftstack.com/howto/java/args-java/?utm
//Crear un archivo con mismo contenido: https://www.tutorialspoint.com/java/java_files_io.htm
//Uso de printf: https://www.it.uc3m.es/pbasanta/asng/course_notes/input_output_printf_es.html
//Determinar cuando se cierra una comilla simple: https://es.stackoverflow.com/questions/137583/determinar-cuantas-vocales-tiene-una-cadena-en-java
package proyecto1;

import java.io.*;

public class Proyecto1 {
  public static void main(String[] args) {

    //Verificar en el cmd que se este ingresando el nombre del archivo
    if (args.length == 0) {
      System.out.println("Por favor indique el nombre del archivo con la extensión .pas");
      return;
    }

    String entradaCMD = args[0]; //Obtener el archivo .pas
    String nombreArchivo = entradaCMD.replace(".pas", ""); //Reemplazar el .pas para solo tener el nombre del archivo
    String archivoErrores = nombreArchivo + "-errores.err";

    try (
      BufferedReader entrada = new BufferedReader(new FileReader(entradaCMD)); //Leer el archivo indicado en el cmd
      PrintWriter salida = new PrintWriter(new FileWriter(archivoErrores)) //Crear el archivo de errores
    ) {
      //Variables de control
      String linea;
      int numeroLinea = 1;
      boolean programOriginal = false;
      boolean esperandoUses = false;
      boolean constCorrecto = false;
      boolean despuesVar = false;
      boolean endEncontrado = false;
      boolean beginEncontrado = false;

      //Mientras hallan líneas para leer
      while ((linea = entrada.readLine()) != null) {
        String lineaTrim = linea.trim().toLowerCase(); //Eliminar espacios sobrantes y convertir a minúscula 
        salida.printf("%04d %s%n", numeroLinea, linea); //Imprimir las líneas junto con su número

        if (!programOriginal) {

          //Verificar que no haya contenido antes de program
          if (lineaTrim.startsWith("//") || lineaTrim.startsWith("{") || linea.isEmpty()) {
            salida.printf("Error 204. Línea %04d. No pueden haber líneas, espacios o comentarios antes de 'program'%n", numeroLinea);
            break;
          }

          //Verificar que el archivo .pas inicie con program
          if (!lineaTrim.startsWith("program")) {
            salida.printf("Error 204. Línea %04d. Debe comenzar con 'program'%n", numeroLinea);
            break;
          }

          //Comprobar que termine en punto y coma
          if (!lineaTrim.endsWith(";")) {
            salida.printf("Error 202. Línea %04d. Falta punto y coma al final%n", numeroLinea);
          }

          //Comprobar el nombre después de 'program'
          String nombreEnLinea = lineaTrim.substring(7).replace(";", "").trim();
          if (!nombreEnLinea.equalsIgnoreCase(nombreArchivo)) {
            salida.printf("Error 201. Línea %04d. El nombre '%s' no coincide con el archivo '%s'%n",
              numeroLinea, nombreEnLinea, nombreArchivo);
          }

          programOriginal = true;
          esperandoUses = true;

        } else if (esperandoUses) {
          if (lineaTrim.isEmpty() || lineaTrim.startsWith("//") || lineaTrim.startsWith("{")) {
            salida.printf("Error 205. Línea %04d. No puede haber nada entre 'program' y 'uses'%n", numeroLinea);
            break;
          }
          //Verificar que exista uses despues de program
          if (!lineaTrim.startsWith("uses")) {
            salida.printf("Error 206. Línea %04d. Debe aparecer 'uses' después de 'program'%n", numeroLinea);
            break;
          }

          if (!lineaTrim.endsWith(";")) {
            salida.printf("Error 207. Línea %04d. La línea 'uses' debe terminar en punto y coma%n", numeroLinea);
          }

          esperandoUses = false; //Ya se encontro el uses 
          if (!esperandoUses) {

          }
        } else {
          //Verificar que no se repita "program"
          for (String palabra: lineaTrim.split("[^a-zA-Z0-9_]+")) {
            if (palabra.equalsIgnoreCase("program")) {
              salida.printf("Error 203. Línea %04d. 'program' está repetido%n", numeroLinea);
              break;
            }
          }
        }

        //validar los const
        if (lineaTrim.startsWith("uses")) {
          constCorrecto = true; //Se permiten const
        }

        if (lineaTrim.startsWith("var")) {
          constCorrecto = false; //ya no se permiten const
        }
        if (lineaTrim.startsWith("const")) {
            
          if (!constCorrecto) {
            salida.printf("Error 210. Línea %04d. 'const' debe estar entre uses y var%n", numeroLinea);
          }
          if (!lineaTrim.contains("=")) {
            salida.printf("Error 210. Línea %04d. 'const' debe contener un '=' despues del identificador%n", numeroLinea);
          }
          if (!lineaTrim.endsWith(";")) {
            salida.printf("Error 210. Línea %04d. 'const' debe contener un ';' al final%n", numeroLinea);
          }
        }
        //Validaciones del begin
        if (lineaTrim.startsWith("var")) {
          despuesVar = true; //Marcar que ya se encontraron declaraciones var
        }

        if (lineaTrim.startsWith("begin")) {
          //Validar que aparezca después de var
          if (!despuesVar) {
            salida.printf("Error 211. Línea %04d. 'begin' debe aparecer después de la declaración de var%n", numeroLinea);
          }

          //Validar que begin esté solo en su línea
          if (!lineaTrim.equals("begin")) {
            salida.printf("Error 212. Línea %04d. 'begin' debe estar solo en su línea, sin otras palabras ni comentarios%n", numeroLinea);
          }
          beginEncontrado = true;
        }
        //Validaciones del end
        if (linea.startsWith("end")) {
          if (lineaTrim.equals("end.")) {
            
            endEncontrado = true;//Es el final correcto

            // Verificar que no tenga comentarios
            if (linea.contains("//") || linea.contains("{")) {
              salida.printf("Error 213. Línea %04d. No se permiten comentarios en la línea de 'end.'%n", numeroLinea);
            }
          } else {
            //Comprobar que sea especificamente "end."
            salida.printf("Error 214. Línea %04d. el end final solo puede aparecer de la siguiente manera 'end.' más%n", numeroLinea);
            endEncontrado = true;
          }
        } else if (endEncontrado) {
          //Verificar que despues del end. no haya nada
          salida.printf("Error 215. Línea %04d. No puede haber líneas después de 'end.'%n", numeroLinea);
        }

        //Validaciones de los write
        if (lineaTrim.startsWith("write")) {
          if (beginEncontrado) {
            //No hacer nada
          } else {
            salida.printf("Error 216. Línea %04d. 'write' no puede estar antes de begin'%n", numeroLinea);
          }
          if (!linea.contains("(") || !linea.contains(")")) {
            salida.printf("Error 217. Línea %04d. la línea del 'write' no contiene uno de sus paréntesis %n", numeroLinea);
          } else {
            //Obtener el contenido que esta adentro despues del primer y ultimo paréntesis
            String contenido = linea.substring(linea.indexOf("(") + 1, linea.lastIndexOf(")")).trim();

            if (contenido.isEmpty()) {
              salida.printf("Error 219. Línea %04d. los paréntesis no pueden ir vacíos%n", numeroLinea);
            } else {
              boolean dentroComilla = false;
              boolean errorCadena = false; //Evitar que el error se muestre más de una vez
              for (int i = 0; i < contenido.length(); i++) {
                char c = contenido.charAt(i);

                if (c == '\'') {
                  dentroComilla = !dentroComilla; //Se abrio la comilla
                  if (!dentroComilla) {
                    errorCadena = false; //Se cerro la comilla
                  }
                } else if (dentroComilla && c == ',' && !errorCadena) {
                  salida.printf("Error 220. Línea %04d. falta comilla antes de la coma%n", numeroLinea);
                  errorCadena = true;
                }
              }

              //Comprobar que las comillas si se cierran
              if (dentroComilla && !errorCadena) {
                salida.printf("Error 220. Línea %04d. falta cerrar comilla en la cadena del paréntesis%n", numeroLinea);
              }
            }
          }
          if(!lineaTrim.endsWith(";")){
              salida.printf("Error 221. Línea %04d. falta el punto y coma del write%n", numeroLinea);
          }
        }
        numeroLinea++; //Aumentar el número de líneas
      }

      System.out.println("Archivo de errores creado: " + archivoErrores);

    } catch (IOException e) {
      System.err.println("Error al leer o escribir el archivo: " + e.getMessage());
    }
  }
}