package com.example.quizoo.model;


import android.Manifest;
import android.content.Context;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.quizoo.R;
import com.example.quizoo.model.dao.UserDao;
import com.example.quizoo.model.entity.Contact;
import com.example.quizoo.model.entity.User;
import com.example.quizoo.model.room.UserDatabase;
import com.example.quizoo.rest.client.CardClient;
import com.example.quizoo.rest.client.QuestionClient;
import com.example.quizoo.rest.pojo.Card;
import com.example.quizoo.rest.pojo.DBResponse;
import com.example.quizoo.rest.pojo.Question;
import com.example.quizoo.util.OnDBResponseListener;
import com.example.quizoo.util.ThreadPool;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class Repository {

    private Context context;
    private UserDao userDao;

    private final static String REST_URL="https://informatica.ieszaidinvergeles.org:9039/PSP/QuiZoo/public/api/";
    private Retrofit retrofit;
    private CardClient cardClient;
    private QuestionClient questionClient;

    public final static String[] CONTACTS_PERMISSION = { Manifest.permission.READ_CONTACTS };
    public final static String[] STORAGE_PERMISSION = { Manifest.permission.READ_EXTERNAL_STORAGE };
    public final static int CONTACTS_PERMISSION_CODE = 1;
    public final static int STORAGE_PERMISSION_CODE = 2;

    private final static String SHARED_PREFERENCE_NAME = "adminData";
    private final static String SHARED_PREFERENCE_KEY = "password";

    public final static int MAX_QUESTIONS_PER_CARD = 5;

    private OnDBResponseListener responseListener;

    private LiveData<List<User>> liveUserList;
    private MutableLiveData<ArrayList<Card>> liveCards;
    private MutableLiveData<ArrayList<Question>> liveQuestions;
    private MutableLiveData<DBResponse> liveResponse;

    private MutableLiveData<ArrayList<Contact>> liveContacts;

    public final static int SCORE_MULTIPLIER = 10; //La puntuación del usuario es el número de preguntas acertadas x 10

    private User currentUser;
    private Card currentCard;
    private Question currentQuestion;

    private MutableLiveData<Long> liveUserInsertId = new MutableLiveData<>();

    public Repository(Context context){
        this.context = context;
        UserDatabase db = UserDatabase.getDb(context);
        userDao = db.getUserDao();
        liveUserList = userDao.getAll();

        liveQuestions = new MutableLiveData<>();
        liveCards = new MutableLiveData<>();
        liveResponse = new MutableLiveData<>();
        liveContacts = new MutableLiveData<>();
        retrofit = new Retrofit.Builder()
                .baseUrl(REST_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        cardClient = retrofit.create(CardClient.class);
        questionClient = retrofit.create(QuestionClient.class);
    }



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
     * Es por eso que la solución que se ha implementado es crear una interfaz (paquete Util) que se
     * encargue de enviar la respuesta del servidor al fragmento que lo necesite.
     */

    public void setResponseListener(OnDBResponseListener listener){ this.responseListener = listener; }






    /* ----- NAVEGACIÓN ----- */

    public void setCurrentUser(User currentUser) { this.currentUser = currentUser; }
    public User getCurrentUser() { return currentUser; }

    public Card getCurrentCard() { return currentCard; }
    public void setCurrentCard(Card currentCard) { this.currentCard = currentCard; }

    public Question getCurrentQuestion() { return currentQuestion; }
    public void setCurrentQuestion(Question currentQuestion) { this.currentQuestion = currentQuestion; }





    /* --------- MÉTODOS DE LA BASE DE DATOS INTERNA --------- */

    public LiveData<List<User>> getLiveUserList(){
        return liveUserList;
    }
    public MutableLiveData<Long> getLiveFriendInsertId() {
        return liveUserInsertId;
    }
    public LiveData<User> getLiveUser(long id) {
        return userDao.getLiveUser(id);
    }


    //Suma 1 al número de respuestas correctas que ha respondido el usuario.
    public void sumUserScore(long id) {
        ThreadPool.threadExecutorPool.execute(new Runnable() {
            @Override
            public void run() {
                userDao.sumUserScore(id);
            }
        });
    }

    //Suma 1 al número de respuestas generales que ha respondido el usuario.
    public void sumUserResponses(long id){
        ThreadPool.threadExecutorPool.execute(new Runnable() {
            @Override
            public void run() {
                userDao.sumUserResponses(id);
            }
        });
    }


    // Inserción de usuario en la base de datos interna (DAO)
    public void insert(User user) {
        ThreadPool.threadExecutorPool.execute(new Runnable() {
            @Override
            public void run() {
                userDao.insert(user);
            }
        });
    }

    // Actualizacion del usuario en la base de datos interna (DAO)
    public void update(User user) {
        ThreadPool.threadExecutorPool.execute(new Runnable() {
            @Override
            public void run() {
                userDao.update(user);
            }
        });
    }

    // Borrado del usuario en la base de datos interna (DAO)
    public void delete(long id){
        ThreadPool.threadExecutorPool.execute(new Runnable() {
            @Override
            public void run() {
                userDao.delete(id);
            }
        });
    }





    /* ---- METODOS QUE CONTROLAN LAS SHARED PREFERENCES ---- */

    /* checkAdminPassword( String: password )
     * Se pasa una contraseña como parámetro, y devuelve true si esa contraseña es la almacenada
     * en las SharedPreferences, false si no.
     */
    public boolean checkAdminPassword(String password){
        SharedPreferences preferences = context.getSharedPreferences(SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE);
        String value = preferences.getString(SHARED_PREFERENCE_KEY, "");
        return value.equals(password);
    }


    /* isSetAdminPassword()
     * Devuelve true si hay una contraseña de admin establecida en SharedPreferences, false si no.
     * Al entrar a la aplicación, se comprueba este método para hacer navegación condicional hacia
     * el Login del Admin.
     */
    public boolean isSetAdminPassword() {
        SharedPreferences preferences = context.getSharedPreferences(SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE);
        String password = preferences.getString(SHARED_PREFERENCE_KEY, "");
        return !password.equals("");
    }


    /* setAdminPassword( String: password )
     * Establece en las SharedPreferences la contraseña de Admin pasada por parámetro
     */
    public void setAdminPassword(String password) {
        SharedPreferences preferences = context.getSharedPreferences(SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE);
        preferences.edit().putString(SHARED_PREFERENCE_KEY, password).apply();
    }




    /* ---------- ALMACENAMIENTO EXTERNO ---------- */


    /* getImageFileFromUri(Uri: uri)
     * Devuelve un objeto File correspondiente al mapa de bits de la imagen de la URI pasada por parámetro.
     * Al abrir un intent de elegir una imagen de la galería, devuelve una URI "protegida", la cual sirve
     * para mostrarla por ejemplo en un ImageView, pero no para subirla a un servidor. Es por eso que se
     * necesita acceder al almacenamiento para obtener el archivo de mapa de bits de esa imagen.
     *
     * Nota: asegurar que los permisos de almacenamiento están concedidos antes de llamar a este método.
     */
    public File getImageFileFromUri(Uri uri){
        String[] filePathColumn = { MediaStore.Images.Media.DATA };
        Cursor cursor = context.getContentResolver().query(uri,
                filePathColumn, null, null, null);
        cursor.moveToFirst();
        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
        String picturePath = cursor.getString(columnIndex);
        cursor.close();
        return new File(picturePath);
    }





    /* ---- METODOS QUE CONTROLAN SI EXISTEN LOS PERMISOS REQUERIDOS ---- */

    /* En la ejecución del programa, los permisos necesarios se piden en el momento de utilizarlos.
     * Por experiencia de usuario, si se preguntan los permisos nada más abrir la aplicación, el usuario
     * se puede sentir intimidado, ya que puede no conocer toda la funcionalidad que tiene la aplicación.
     * Por eso, es preferible tratar cada permiso individualmente y pedirlo en el momento que el usuario
     * vaya a sacar provecho a esa funcionalidad.
     *
     * Además, siempre que se vaya a sacar partido a una funcionalidad que requiera permisos, es importante
     * comprobar que los tiene, porque puede haberlos revocado en los ajustes del sistema.
     */


    /* contactsPermissionIsGranted()
     * Devuelve true si el permiso de acceder a los contactos está concedido, false si no.
     */
    public boolean contactsPermissionIsGranted(){
        if(Build.VERSION.SDK_INT<Build.VERSION_CODES.M) { return true; }
        return context.checkSelfPermission(Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED;
    }

    /* storagePermissionIsGranted()
     * Devuelve true si el permiso de acceder al almacenamiento externo está concedido, false si no.
     * Se utiliza para elegir imagen de la galería para subirla al servidor.
     */
    public boolean storagePermissionIsGranted(){
        if(Build.VERSION.SDK_INT<Build.VERSION_CODES.M) { return true; }
        return context.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }






    /* ---- METODO QUE OBTIENE LOS CONTACTOS DEL TELEFONO CON EMAIL ---- */

    public MutableLiveData<ArrayList<Contact>> getLiveContacts() { return liveContacts; }

    /* loadContactsWithMail()
     * En una hebra, carga en el MutableLiveData liveContacts los contactos que tienen e-mail.
     */
    public void loadContactsWithMail(){
        ThreadPool.threadExecutorPool.execute(new Runnable() {
            @Override
            public void run() {
                ArrayList<Contact> contacts = new ArrayList<>();

                String[] data = new String[]{ContactsContract.Data.DISPLAY_NAME, ContactsContract.CommonDataKinds.Email.ADDRESS};
                String order = ContactsContract.Data.DISPLAY_NAME + " ASC";
                String selectionEmail = ContactsContract.Data.MIMETYPE + "='" +
                        ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE+ "' AND "
                        + ContactsContract.CommonDataKinds.Email.ADDRESS + " IS NOT NULL";

                Cursor cursor =  context.getContentResolver().query(
                        ContactsContract.Data.CONTENT_URI,
                        data,
                        selectionEmail,
                        null,
                        order);

                while(cursor.moveToNext()){
                    String name = cursor.getString(0);
                    String email = cursor.getString(1);

                    Contact contact = new Contact(name, email);
                    contacts.add(contact);
                }
                cursor.close();
                liveContacts.postValue(contacts);
            }
        });
    }


    /* ---- METODO PARA MANDAR CORREO A TRAVES DE INTENT ---- */

    /* mandarCorreo( String: correo, String: puntuación )
     * Puntuación: cuerpo del correo. En la aplicación, sólo se envía correo con puntuaciones.
     * Abre un intent con el que se busca qué servicio puede enviar un correo al destinatario
     * del parámetro correo, con la puntuación como cuerpo.
     */
    public void mandarCorreo(String correo, String puntuacion){
        String[] TO = {correo};
        Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
        emailIntent.setData(Uri.parse("mailto:"));

        emailIntent.putExtra(Intent.EXTRA_EMAIL, TO);

        emailIntent.putExtra(Intent.EXTRA_SUBJECT, context.getString(R.string.mail_subject));
        emailIntent.putExtra(Intent.EXTRA_TEXT, puntuacion);

        String title = context.getString(R.string.mail_intent_title);

        if(emailIntent.resolveActivity(context.getPackageManager()) != null){
            context.startActivity(Intent.createChooser(emailIntent, title).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
        }
    }






    /* -------------------- BASE DE DATOS CON RETROFIT -------------------- */




    /* ---- METODOS QUE CARGAN LAS CARTAS PARA LA EL JUEGO Y ADMINISTRACIÓN ---- */

    public MutableLiveData<ArrayList<Card>> getLiveCards() {
        return liveCards;
    }



    /* loadCardsForGame()
     * En este método se carga en el MutableLiveData liveCards las cartas para empezar un juego.
     * La llamada que hace en Retrofit corresponde a una ruta en el servidor, la cual devuelve una lista
     * de cartas en orden aleatorio, y cada carta con su lista de preguntas también en orden aleatorio.
     * Las preguntas se guardan en el ArrayList de la POJO Card.
     */
    public void loadCardsForGame(){
        Call<ArrayList<Card>> call = cardClient.getAllCardsWithQuestions();
        call.enqueue(new Callback<ArrayList<Card>>() {
            @Override
            public void onResponse(Call<ArrayList<Card>> call, Response<ArrayList<Card>> response) {
                liveCards.setValue(response.body());
            }

            @Override
            public void onFailure(Call<ArrayList<Card>> call, Throwable t) {
                liveCards.setValue(null);
            }
        });

    }

    /* loadCards()
     * En este método se carga en el MutableLiveData liveCards las cartas que hay en el servidor, sin
     * asociarle preguntas en el ArrayList.
     * Este método se usa para listar las cartas en la zona de administración.
     */
    public void loadCards(){
        Call<ArrayList<Card>> call = cardClient.getAllCards();
        call.enqueue(new Callback<ArrayList<Card>>() {
            @Override
            public void onResponse(Call<ArrayList<Card>> call, Response<ArrayList<Card>> response) {
                liveCards.setValue(response.body());
            }

            @Override
            public void onFailure(Call<ArrayList<Card>> call, Throwable t) {
                liveCards.setValue(null);
            }
        });
    }



    /* ---- INSERCIÓN, ACTUALIZACION, BORRADO Y OBTENCIÓN DE LAS CARTAS DEL SERVIDOR ---- */


    /* addCard( Card: card )
     * Intenta subir al servidor la carta especificada por parámetro.
     * En caso de haberse insertado con éxito, se llama a la interfaz responseListener definida en esta
     * clase con el método onSuccess(), en caso de fallo, con onFailure().
     *
     * Leer al principio de esta clase Repository el por qué de usar esa interfaz.
     */
    private void addCard(Card card){
        Call<DBResponse> request = cardClient.addCard(card);

        request.enqueue(new Callback<DBResponse>() {
            @Override
            public void onResponse(Call<DBResponse> call, Response<DBResponse> response) {
                if(response.body() == null || response.body().getResult() == false){
                    responseListener.onFailed();
                }else{
                    responseListener.onSuccess(response.body());
                }
            }

            @Override
            public void onFailure(Call<DBResponse> call, Throwable t) {
                responseListener.onFailed();
            }
        });
    }


    /* addCard( Uri: imageUri, Card: card)
     * Este método añade una carta pasada por parámetro, pudiendo establecer también su imagen
     *
     * El parámetro imageUri corresponde a la imageUri que devuelve la actividad de haber seleccionado
     * una imagen de la galería. Para sacar un archivo File de esa URI con su mapa de bits, es necesario
     * hacerlo accediendo al almacenamiento externo, siempre comprobando que los permisos están concedidos.
     * El método getImageFileFromUri(Uri: imageUri) está definido en esta clase.
     * Cuando se obtiene el objeto File, hay que crear un Body de Multipart Form-Data, que es el tipo de
     * contenido que se acepta para subir imágenes. A continuación, se intenta subir al servidor.
     *
     * Cuando el servidor responde a la subida de la imagen, se comprueba si se ha guardado correctamente.
     * En caso de haberse guardado correctamente la imagen, devuelve la URL de la imagen pública, la cual
     * usamos para asignarla como propiedad al objeto Card. A continuación, se subiría al servidor la carta
     * con su propiedad URL de la imagen bien puesta.
     *
     * En caso de no haberse guardado correctamente la imagen, se ignora ese campo y se intenta subir la carta
     * sin haber asignado URL de imagen, y la base de datos coge el valor por defecto que se le ha configurado,
     * que es una URL a una imagen genérica.
     */
    public void addCard(Uri imageUri, Card card){
        if(imageUri == null){
            addCard(card);
        }else {
            if (storagePermissionIsGranted()) {
                File imageFile = getImageFileFromUri(imageUri);
                RequestBody requestFile =
                        RequestBody.create(
                                MediaType.parse(context.getContentResolver().getType(imageUri)),
                                imageFile
                        );

                MultipartBody.Part body =
                        MultipartBody.Part.createFormData("image", imageFile.getName(), requestFile);

                Call<DBResponse> request = cardClient.saveImage(body);

                request.enqueue(new Callback<DBResponse>() {
                    @Override
                    public void onResponse(Call<DBResponse> call, Response<DBResponse> response) {
                        if (response.body() != null && response.body().getResult() == true) {
                            card.setPictureUrl(response.body().getUrl());
                        }
                        addCard(card);
                    }

                    @Override
                    public void onFailure(Call<DBResponse> call, Throwable t) {
                        //Si ha fallado subir la imagen al servidor, se sube sin imagen
                        addCard(card);
                    }
                });
            }
        }
    }



    /* updateCard( Card: card )
     * Intenta actualizar la carta especificada en el servidor.
     * En caso de haberse actualizado con éxito, se llama a la interfaz responseListener definida en esta
     * clase con el método onSuccess(), en caso de fallo, con onFailure().
     *
     * Leer al principio de esta clase Repository el por qué de usar esa interfaz.
     */
    private void updateCard(Card card){
        Call<DBResponse> request = cardClient.updateCard(card.getId(), card);

        request.enqueue(new Callback<DBResponse>() {
            @Override
            public void onResponse(Call<DBResponse> call, Response<DBResponse> response) {
                if(response.body() == null || response.body().getResult() == false){
                    responseListener.onFailed();
                }else{
                    responseListener.onSuccess(response.body());
                }
            }

            @Override
            public void onFailure(Call<DBResponse> call, Throwable t) {
                responseListener.onFailed();
            }
        });
    }


    /* updateCard( Uri: imageUri, Card: card)
     * Este método actualiza los datos de la carta pasada por parámetro, pudiendo actualizar también
     * su imagen.
     *
     * El parámetro imageUri corresponde a la imageUri que devuelve la actividad de haber seleccionado
     * una imagen de la galería. Para sacar un archivo File de esa URI con su mapa de bits, es necesario
     * hacerlo accediendo al almacenamiento externo, siempre comprobando que los permisos están concedidos.
     * El método getImageFileFromUri(Uri: imageUri) está definido en esta clase.
     * Cuando se obtiene el objeto File, hay que crear un Body de Multipart Form-Data, que es el tipo de
     * contenido que se acepta para subir imágenes. A continuación, se intenta subir al servidor.
     *
     * Cuando el servidor responde a la subida de la imagen, se comprueba si se ha guardado correctamente.
     * En caso de haberse guardado correctamente la imagen, devuelve la URL de la imagen pública, la cual
     * usamos para asignarla como propiedad al objeto Card. A continuación, se subiría al servidor la carta
     * con su propiedad URL de la imagen bien puesta.
     *
     * En caso de no haberse guardado correctamente la imagen, se ignora ese campo y se intenta subir la carta
     * sin haber asignado URL de imagen, y la base de datos coge el valor por defecto que se le ha configurado,
     * que es una URL a una imagen genérica.
     */
    public void updateCard(Uri imageUri, Card card){
        if(imageUri == null){
            updateCard(card);
        }else {
            if (storagePermissionIsGranted()) {
                File imageFile = getImageFileFromUri(imageUri);
                RequestBody requestFile =
                        RequestBody.create(
                                MediaType.parse(context.getContentResolver().getType(imageUri)),
                                imageFile
                        );

                MultipartBody.Part body =
                        MultipartBody.Part.createFormData("image", imageFile.getName(), requestFile);

                Call<DBResponse> request = cardClient.saveImage(body);

                request.enqueue(new Callback<DBResponse>() {
                    @Override
                    public void onResponse(Call<DBResponse> call, Response<DBResponse> response) {
                        if (response.body() != null && response.body().getResult() == true) {
                            card.setPictureUrl(response.body().getUrl());
                        }
                        updateCard(card);
                    }

                    @Override
                    public void onFailure(Call<DBResponse> call, Throwable t) {
                        //Si ha fallado subir la imagen al servidor, se sube sin imagen
                        updateCard(card);
                    }
                });
            }
        }
    }


    /* deleteCard( Card: card )
     * Intenta eliminar la carta especificada en el servidor.
     * En caso de haberse eliminado con éxito, se llama a la interfaz responseListener definida en esta
     * clase con el método onSuccess(), en caso de fallo, con onFailure().
     *
     * Leer al principio de esta clase Repository el por qué de usar esa interfaz.
     */
    public void deleteCard(Card card){
        Call<DBResponse> request = cardClient.deleteCard(card.getId());

        request.enqueue(new Callback<DBResponse>() {
            @Override
            public void onResponse(Call<DBResponse> call, Response<DBResponse> response) {
                if(response.body() == null || response.body().getResult() == false){
                    responseListener.onFailed();
                }else{
                    responseListener.onSuccess(response.body());
                }
            }

            @Override
            public void onFailure(Call<DBResponse> call, Throwable t) {
                responseListener.onFailed();
            }
        });
    }



    /* ---- INSERCIÓN, ACTUALIZACION, BORRADO Y OBTENCIÓN DE LAS PREGUNTAS DEL SERVIDOR ---- */

    public MutableLiveData<ArrayList<Question>> getLiveQuestions() {
        return liveQuestions;
    }

    /* addQuestion( Question: question )
     * Intenta subir al servidor la pregunta especificada por parámetro.
     * En caso de haberse insertado con éxito, se llama a la interfaz responseListener definida en esta
     * clase con el método onSuccess(), en caso de fallo, con onFailure().
     *
     * Leer al principio de esta clase Repository el por qué de usar esa interfaz.
     */
    public void addQuestion(Question question){
        Call<DBResponse> request = questionClient.addQuestion(question);

        request.enqueue(new Callback<DBResponse>() {
            @Override
            public void onResponse(Call<DBResponse> call, Response<DBResponse> response) {
                if(response.body()!=null) Log.v("xyzyx", "MENSAJE "+response.body().toString());
                if(response.body() == null || response.body().getResult() == false){
                    responseListener.onFailed();
                }else{
                    responseListener.onSuccess(response.body());
                }
            }

            @Override
            public void onFailure(Call<DBResponse> call, Throwable t) {
                responseListener.onFailed();
            }
        });
    }


    /* updateQuestion( Question: question )
     * Intenta actualizar la pregunta especificada en el servidor.
     * En caso de haberse actualizado con éxito, se llama a la interfaz responseListener definida en esta
     * clase con el método onSuccess(), en caso de fallo, con onFailure().
     *
     * Leer al principio de esta clase Repository el por qué de usar esa interfaz.
     */
    public void updateQuestion(Question question){
        Call<DBResponse> request = questionClient.updateQuestion(question.getId(), question);

        request.enqueue(new Callback<DBResponse>() {
            @Override
            public void onResponse(Call<DBResponse> call, Response<DBResponse> response) {
                if(response.body() == null || response.body().getResult() == false){
                    responseListener.onFailed();
                }else{
                    responseListener.onSuccess(response.body());
                }
            }

            @Override
            public void onFailure(Call<DBResponse> call, Throwable t) {
                responseListener.onFailed();
            }
        });
    }



    /* deleteQuestion( Question: question )
     * Intenta eliminar la pregunta especificada en el servidor.
     * En caso de haberse eliminado con éxito, se llama a la interfaz responseListener definida en esta
     * clase con el método onSuccess(), en caso de fallo, con onFailure().
     *
     * Leer al principio de esta clase Repository el por qué de usar esa interfaz.
     */
    public void deleteQuestion(Question question){
        Call<DBResponse> request = questionClient.deleteQuestion(question.getId());

        request.enqueue(new Callback<DBResponse>() {
            @Override
            public void onResponse(Call<DBResponse> call, Response<DBResponse> response) {
                if(response.body() == null || response.body().getResult() == false){
                    responseListener.onFailed();
                }else{
                    responseListener.onSuccess(response.body());
                }
            }

            @Override
            public void onFailure(Call<DBResponse> call, Throwable t) {
                responseListener.onFailed();
            }
        });
    }


    /* loadQuestionsOf()
     * En este método carga en el MutableLiveData liveQuestions las preguntas asociadas a la carta
     * especificada.
     * La llamada que hace en Retrofit corresponde a una ruta en el servidor, la cual pasándole como
     * parámetro el ID de una carta, devuelve sólo las preguntas asociadas a esa carta, en orden
     * de creación descendiente.
     * Este método se llama desde la parte de administración, cuando se pulsa sobre una carta del
     * RecyclerView de cartas, para mostrar las preguntas que tiene asociadas en ese momento.
     */
    public void loadQuestionsOf(Card card){
        Call<ArrayList<Question>> request = questionClient.getQuestionsOf(card.getId());

        request.enqueue(new Callback<ArrayList<Question>>() {
            @Override
            public void onResponse(Call<ArrayList<Question>> call, Response<ArrayList<Question>> response) {
                liveQuestions.setValue(response.body());
            }

            @Override
            public void onFailure(Call<ArrayList<Question>> call, Throwable t) {
                liveQuestions.setValue(null);
            }
        });
    }






    //Finalmente no utilizado (leer al principio de esta clase Repository)
    public LiveData<DBResponse> getLiveResponse() { return liveResponse; }

}
