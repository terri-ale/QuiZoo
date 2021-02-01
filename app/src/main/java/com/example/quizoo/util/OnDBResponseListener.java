package com.example.quizoo.util;

import com.example.quizoo.rest.pojo.DBResponse;

/* Haciendo pruebas en fragmentos que necesitan una respuesta DBResponse (respuesta de cambios guardados
 * correctamente, como editar, borrar, añadir carta...), si se observaba el MutableLiveData de DBResponse,
 * daba problema.
 *
 * Cuando se editaba / añadía una pregunta o carta por primera vez funcionaba bien, pero cuando se volvía otra
 * vez al fragmento, crasheaba la aplicación por llamar a elementos gráficos desde un contexto nulo.
 * Se ha llegado a la conclusión de que al declarar un Observer del MutableLiveData, la primera vez que se entra
 * al fragmento t odo se ejecuta bien, pero al volver al fragmento anterior el Observer no se destruye y se
 * queda "colgando", y al volver otra vez al fragmento de gestionar la carta o pregunta, el anterior Observer
 * seguía con vida, pero bajo un contexto vacío.
 *
 * Es por eso que la solución que se ha implementado es crear esta interfaz que se
 * encargua de enviar la respuesta del servidor al fragmento que lo necesite.
 */

public interface OnDBResponseListener {
    public void onSuccess(DBResponse response);
    public void onFailed();
}
