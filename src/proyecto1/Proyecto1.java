//Leer desde el cmd: https://www.delftstack.com/howto/java/args-java/?utm
//Crear un archivo con mismo contenido: https://www.tutorialspoint.com/java/java_files_io.htm
//Uso de printf: https://www.it.uc3m.es/pbasanta/asng/course_notes/input_output_printf_es.html
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
        String nombreArchivo = entradaCMD.replace(".pas", "");//Reemplazar el .pas para solo tener el nombre del archivo
        String archivoErrores = nombreArchivo + "-errores.err";

        try (
            BufferedReader entrada = new BufferedReader(new FileReader(entradaCMD));//Leer el archivo indicado en el cmd
            PrintWriter salida = new PrintWriter(new FileWriter(archivoErrores))//Crear el archivo de errores
        ) {
            //Variables de control
            String linea;
            int numeroLinea = 1;
            boolean programOriginal = false;
            boolean esperandoUses = false;
            
            //Mientras hallan líneas para leer
            while ((linea = entrada.readLine()) != null) {
                String lineaTrim = linea.trim().toLowerCase();//Eliminar espacios sobrantes y convertir a minúscula 
                salida.printf("%04d %s%n", numeroLinea, linea);//Imprimir las líneas junto con su número

                if (!programOriginal) {
                    
                    //Verificar que no haya contenido antes del program
                    if (lineaTrim.startsWith("//") || lineaTrim.startsWith("{") || linea.isEmpty()) {
                        salida.printf("Error 204. Línea %04d. No pueden haber líneas, espacios o comentarios antes de 'program'%n", numeroLinea);
                        break;
                    }
                    
                    //Verificar que el .pas inicie con program
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

                    esperandoUses = false;//Ya se encontro el uses 

                } else {
                    // Comprobar que no se repita 'program' después
                    if (lineaTrim.contains("program")) {
                        salida.printf("Error 203. Línea %04d. 'program' está repetido%n", numeroLinea);
                    }
                }

                numeroLinea++;//Aumentar el número de líneas
            }

            System.out.println("Archivo de errores creado: " + archivoErrores);

        } catch (IOException e) {
            System.err.println("Error al leer o escribir el archivo: " + e.getMessage());
        }
    }
}