package com.example.quizoo.rest.pojo;

public class DBResponse {

    private long id;
    private String url;         // Usada para la subida de imágenes

    private boolean result;     // Inserción, edición o borrado correcto o incorrecto

    private String message;     // Para cuando se quiera devolver desde servidor un mensaje de error, depuración ( $e->getMessage() )

    public DBResponse(){ }

    public long getId() {
        return id;
    }

    public String getUrl() {
        return url;
    }

    public boolean getResult() {
        return result;
    }

    @Override
    public String toString() {
        return "DBResponse{" +
                "id=" + id +
                ", url='" + url + '\'' +
                ", result=" + result +
                ", message='" + message + '\'' +
                '}';
    }
}
