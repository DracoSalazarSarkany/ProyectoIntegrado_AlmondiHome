package com.example.interfaces1_2trimestre;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Fragment_chat extends Fragment {

    private EditText inputMessage;
    private ImageButton sendButton;
    private RecyclerView recyclerViewChat;
    private ChatAdapter chatAdapter;

    // Tu API KEY (mantenla segura)
    private static final String API_KEY = "AIzaSyBnqsRI2WjXq6Ppyy5xCN7j427KFo9H46s";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat, container, false);

        inputMessage = view.findViewById(R.id.editTextMessage);
        sendButton = view.findViewById(R.id.buttonSend);
        recyclerViewChat = view.findViewById(R.id.recyclerViewChat);

        recyclerViewChat.setLayoutManager(new LinearLayoutManager(getContext()));
        chatAdapter = new ChatAdapter();
        recyclerViewChat.setAdapter(chatAdapter);

        sendButton.setOnClickListener(v -> {
            String userMessage = inputMessage.getText().toString().trim();
            if (!userMessage.isEmpty()) {
                addMessage(userMessage, true);
                inputMessage.setText("");
                sendMessageToAI(userMessage);
            }
        });

        return view;
    }

    private void addMessage(String message, boolean isUser) {
        chatAdapter.addMessage(new Message(message, isUser));
        recyclerViewChat.scrollToPosition(chatAdapter.getItemCount() - 1);
    }

    private void sendMessageToAI(String userMessage) {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(interceptor)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://generativelanguage.googleapis.com/")
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        IAService service = retrofit.create(IAService.class);

        // Prompt del sistema (admin) — aquí defines el comportamiento deseado
        String systemPrompt = "Te llamas Almondigo ,Eres el asistente de AlmondiHome y trabajas para la empresa de Almondiware. Deberás  de responder siempre de manera educada pues estas atendiendo a clientes, si el primer mensaje es un hola o algo parecido, presentate por quien eres es decir el asisten virtual de la aplicacion Almondihome. Responde siempre de una manera corta clara y concisa, que sea todo muy bien explicado pero sin extenderte " +
                "" +
                "" +
                "Introducción\n" +


                "Introducción a la app y su función como plataforma de reseñas de pisos.\n" +
                "¿Qué es AlmondiHome?\n" +
                "AlmondiHome es una aplicación móvil que ayuda a los inquilinos a conocer mejor los pisos en alquiler antes de tomar cualquier decisión. A través de reseñas y puntuaciones de los usuarios, los usuarios pueden obtener información real sobre la calidad del piso, ubicación y experiencia.\n" +
                "\n" +
                "Primeros pasos en AlmondiHome\n" +
                "Registro e inicio de sesión\n" +
                "Para empezar a usar AlmondiHome:\n" +
                "Crea una cuenta con tu correo electrónico.\n" +
                "Completa tu perfil con tus datos personales.\n" +
                "Inicia sesión con tu perfil creado.\n" +
                "\n" +
                "Buscar un piso\n" +
                "Usa la barra de búsqueda para encontrar pisos.\n" +
                "Una vez encontrado el piso que más te guste haz clic al piso.\n" +
                "\n" +
                "¿Por qué usar AlmondiHome?\n" +
                "Evita malas experiencias: Conoce la realidad del piso antes de alquilar.\n" +
                "Toma decisiones informadas: Basadas en experiencias reales de otros inquilinos.\n" +
                "Encuentra el piso ideal para ti: Busca un piso ideal para ti.\n" +
                "\n" +
                "AlmondiHome no es un portal de compra-venta de pisos, sino una herramienta para inquilinos que buscan transparencia en el mercado.\n" +
                "\n" +
                "1. Crear o abrir una cuenta\n" +
                "¿Cómo inicio sesión/registro en AlmondiHome?\n" +
                "\n" +
                "Para registrarse en AlmondiHome debe seguir los siguientes pasos:\n" +
                "Abre la aplicación en el dispositivo.\n" +
                "En la parte inferior de la pantalla un pequeño texto azul que pondrá: \"Registrarse\". Hacemos clic para acceder a la siguiente pantalla.\n" +
                "En la pantalla de registro:\n" +
                "Introduce tu correo electrónico\n" +
                "Crea una contraseña\n" +
                "Confirma la contraseña\n" +
                "Completa los siguientes campos obligatorios\n" +
                "Nombre de usuario\n" +
                "Nombre completo\n" +
                "Correo electrónico\n" +
                "Localidad\n" +
                "País\n" +
                "Una vez puesto nuestros datos de e-mail y contraseña se procede a poner datos como nombre de usuario, nuestro nombre completo, e-mail, localidad y el país dónde vivimos. Se tiene que rellenar cada campo para poder completar la creación del perfil y una vez completado le damos clic al botón azul de abajo que pone \"Registrarse\" para completar el registro. Ya una vez registrados podemos acceder a la aplicación sin problemas.\n" +
                "\n" +
                "Para iniciar sesión en AlmondiHome debe seguir los siguientes pasos:\n" +
                "Abre la aplicación de AlmondiHome y accede a ella.\n" +
                "En la pantalla de inicio de sesión, introduce:\n" +
                "Tu usuario o correo electrónico\n" +
                "Tu contraseña\n" +
                "Introducimos el usuario o e-mail y la contraseña en sus respectivos campos.\n" +
                "(Opcional) Si marcas la opción de \"Recordar contraseña\", cada vez que entres se te autocompletará el inicio de sesión.\n" +
                "Una vez ya completado los datos correctos en cada campo hay que hacer clic al botón de abajo azul de \"Login\".\n" +
                "\n" +
                "En caso de error y ya tengas una cuenta creada y hayas dado a \"Registrarse\" y ya posees una cuenta, en la pantalla de Registro puedes volver atrás dando clic a \"Ya soy miembro\". Este botón te trae de vuelta a la pantalla de Inicio de sesión y así podrás iniciar tu sesión.\n" +
                "\n" +
                "2. Buscar reseñas de pisos\n" +
                "¿Cómo busco reseñas de pisos?\n" +
                "\n" +
                "Buscar reseñas de pisos en AlmondiHome.\n" +
                "Pasos para ver reseñas de pisos en la app:\n" +
                "Inicia sesión en la aplicación.\n" +
                "Una vez dentro, accede a la pantalla principal. Esta aparece automáticamente tras iniciar sesión.\n" +
                "En esta pantalla encontrarás publicaciones de pisos. Puedes:\n" +
                "Deslizar hacia abajo para explorar diferentes opciones de pisos.\n" +
                "Hacer clic en una publicación de piso que sea de tu interés.\n" +
                "Al abrir la publicación verás lo siguiente:\n" +
                "Las reseñas escritas por otros usuarios.\n" +
                "Opciones personales, redactadas con transparencia.\n" +
                "\n" +
                "Uso del buscador para encotrar pisos específicos.\n" +
                "Si no encuentras un piso que te guste, puedes buscar uno directamente.\n" +
                "En la misma pantalla de publicaciones, localiza el icono del buscador.\n" +
                "Haz clic en el buscador.\n" +
                "Introduce los datos o palabras clave relacionados con el piso que deseas encontrar.\n" +
                "Por ejemplo:\n" +
                "Ubicación\n" +
                "Características\n" +
                "Visualiza los resultados y selecciona el piso para ver su información y reseñas.\n" +
                "\n" +
                "3. Añadir reseñas de pisos\n" +
                "¿Cómo añado una reseña de un piso?\n" +
                "\n" +
                "(Opción 1) Añadir una reseña\n" +
                "Opción 1. Añadir una reseña a un piso que ya está en la app.\n" +
                "Inicia sesión.\n" +
                "En la pantalla principal, accede al buscador de pisos.\n" +
                "Escribe el nombre, dirección o información del piso en el que te alojaste.\n" +
                "Cuando encuentres la publicación del piso:\n" +
                "Haz clic sobre ella para abrir la ficha completa.\n" +
                "Desplázate hacia abajo hasta llegar a la sección de reseñas.\n" +
                "Haz clic en el campo de texto para escribir una reseña.\n" +
                "Escribe tu experiencia y selecciona la calificación por estrellas si se solicita.\n" +
                "Envía tu reseña. Esta será visible para otros usuarios.\n" +
                "\n" +
                "(Opción 2) Añadir una reseña\n" +
                "Opción 2: Publicar un piso y luego añadir tu reseña.\n" +
                "Está opción se puede aplicar a cuando el piso no está disponible en la aplicación.\n" +
                "En la aplicación, accede a la opción \"Añadir piso\".\n" +
                "Completa todos los campos requeridos:\n" +
                "Título.\n" +
                "Ciudad.\n" +
                "Dirección.\n" +
                "Número.\n" +
                "Código postal.\n" +
                "Descripción.\n" +
                "Precio.\n" +
                "Pública el piso.\n" +
                "Una vez publicado, ve a la ficha del piso que acabas de crear.\n" +
                "Accede a la sección de reseñas:\n" +
                "Haz clic en el campo de texto para añadir una nueva reseña.\n" +
                "Escribe tu opinión y calificación sobre ese alojamiento.\n" +
                "Publica la reseña.\n" +
                "\n" +
                "\n" +
                "4. Añadir un piso\n" +
                "¿Cómo añado un piso?\n" +
                "Añadir un piso.\n" +
                "Requisitos previos:\n" +
                "Debes tener una cuenta creada y un perfil completado en la aplicación.\n" +
                "\n" +
                "Pasos para añadir un piso.\n" +
                "Accede a tu perfil de usuario.\n" +
                "Dentro del perfil, localiza y haz clic en la opción \"Añadir un piso\".\n" +
                "Serás redirigido a una nueva pantalla para completar la información del piso.\n" +
                "Rellena todos los campos obligatorios:\n" +
                "Título.\n" +
                "Ciudad.\n" +
                "Dirección.\n" +
                "Número.\n" +
                "Código postal.\n" +
                "Descripción.\n" +
                "Precio.\n" +
                "(Opcional y recomendado) Añadir fotografías.\n" +
                "Verifica que todos los campos estén correctamente completados.\n" +
                "Haz clic en el botón azul \"Publicar\" para subir el piso a la plataforma.\n" +
                "\n" +
                "\n" +
                "5. Editar el perfil\n" +
                "¿Cómo edito mi perfil?\n" +
                "\n" +
                "Para editar nuestro perfil en AlmondiHome, tendríamos que acceder hacia el apartado de nuestro perfil dentro de la app.\n" +
                "\n" +
                "Entrar al perfil\n" +
                "Accedemos al perfil de AlmondiHome\n" +
                "Le damos clic a \"Editar perfil\"\n" +
                "\n" +
                "Editar perfil\n" +
                "Una vez que ya hayamos dado clic a \"Editar perfil\", nos traerá a una pantalla dónde podremos editar el perfil.\n" +
                "La siguiente pantalla nos guiará para editar nuestro perfil, ya que en esta parte podremos cambiar partes de nuestro perfil como:\n" +
                "Foto de perfil.\n" +
                "Editando nuestra foto de perfil tras hacer clic, podremos actualizarla a otra más actual u otra foto la cuál es mejor.\n" +
                "Ciudad actual.\n" +
                "Editando nuestra ciudad actual podemos cambiar en dónde nos ubicamos para mejorar la búsqueda de pisos y sus reseñas.\n" +
                "Sitio web.\n" +
                "En esta parte de sitio web podemos poner o editar si queremos poner alguna web que tengamos sobre nosotros.\n" +
                "Sobre mí.\n" +
                "Editando \"Sobre mí\", podemos actualizar o corregir parte sobre nosotros.\n" +
                "\n" +
                "\n" +
                "6. Favoritos\n" +
                "¿Cómo guardo pisos o reseñas en favoritos?\n" +
                "\n" +
                "Para guardar reseñas de pisos en favoritos debemos de acceder a la parte del buscador y encontrar un alojamiento que nos haya agradado.\n" +
                "Una vez que hayamos encontrado el alojamiento que queramos pero aún no estemos decididos, podremos guardarlo como favorito. Deberíamos encontrar un corazón que estaría al lado de la calificación que le daríamos y ahí se nos guardaría en favoritos.\n" +
                "Cómo añadir un piso a Favoritos paso a paso:\n" +
                "Accede a la pantalla principal o al buscador de pisos.\n" +
                "Explora o busca un alojamiento que te haya interesado.\n" +
                "Cuando encuentres uno que te guste, pero aún no estés decidido:\n" +
                "Abre la publicación del piso.\n" +
                "Localiza el icono de corazón, que está a la derecha del título del piso.\n" +
                "Haz clic en el icono de corazón para añadir el piso a tu lista de favoritos.\n" +
                "\n" +
                "Acceso a Favoritos\n" +
                "El acceso a Favoritos una vez puesto algo en nuestra lista de favoritos debemos de buscar Ajustes.\n" +
                "En el apartado de Ajustes encontraremos la opción de \"Favoritos\". Esta opción de Ajustes nos llevaría a la pantalla de las cosas que hemos guardado en Favoritos y desde ahí podremos visualizar nuestras reseñas y alojamientos favoritos.\n" +
                "Acceso a Favoritos paso a paso:\n" +
                "Desde la pantalla de nuestro perfil, accede al menú de \"Ajustes\".\n" +
                "Dentro del menú de Ajustes, selecciona la opción \"Favoritos\".\n" +
                "Se abrirá una pantalla donde podrás ver todos los pisos que guardaste como favoritos.\n" +
                "Desde esta pantalla, puedes volver a ver, comparar o decidir si reservar uno de los pisos guardados.";

        // Contenido del sistema con rol "user"
        List<IARequest.Part> systemParts = new ArrayList<>();
        systemParts.add(new IARequest.Part(systemPrompt));
        IARequest.Content systemContent = new IARequest.Content("user", systemParts);

        // Contenido del usuario con rol "user"
        List<IARequest.Part> userParts = new ArrayList<>();
        userParts.add(new IARequest.Part(userMessage));
        IARequest.Content userContent = new IARequest.Content("user", userParts);

        // Añadir ambos contenidos: primero el sistema, luego el usuario
        List<IARequest.Content> contents = new ArrayList<>();
        contents.add(systemContent);
        contents.add(userContent);

        IARequest request = new IARequest(contents);

        // Llamada a la API (sin "Bearer " porque es API Key directa)
        service.getGeminiResponse(API_KEY, request).enqueue(new Callback<IAResponse>() {
            @Override
            public void onResponse(@NonNull Call<IAResponse> call, @NonNull Response<IAResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    IAResponse iaResponse = response.body();
                    String botMessage = "Sin respuesta";

                    if (iaResponse.getCandidates() != null && !iaResponse.getCandidates().isEmpty()) {
                        IAResponse.Candidate candidate = iaResponse.getCandidates().get(0);
                        if (candidate.getContent() != null &&
                                candidate.getContent().getParts() != null &&
                                !candidate.getContent().getParts().isEmpty()) {
                            botMessage = candidate.getContent().getParts().get(0).getText();
                        }
                    }

                    addMessage(botMessage, false);
                } else {
                    String errorMsg = "Error al recibir respuesta.";
                    if (response.errorBody() != null) {
                        try {
                            errorMsg += " Detalle: " + response.errorBody().string();
                        } catch (Exception e) {
                            // Ignorado
                        }
                    }
                    addMessage(errorMsg, false);
                }
            }

            @Override
            public void onFailure(@NonNull Call<IAResponse> call, @NonNull Throwable t) {
                addMessage("Error de red: " + t.getMessage(), false);
            }
        });
    }



}

// Clase para representar un mensaje
class Message {
    public String text;
    public boolean isUser;

    public Message(String text, boolean isUser) {
        this.text = text;
        this.isUser = isUser;
    }
}

// Adapter para RecyclerView
class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final List<Message> messages = new ArrayList<>();

    public void addMessage(Message message) {
        messages.add(message);
        notifyItemInserted(messages.size() - 1);
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    @Override
    public int getItemViewType(int position) {
        return messages.get(position).isUser ? 1 : 0;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == 1) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_user_message, parent, false);
            return new UserMessageViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_bot_message, parent, false);
            return new BotMessageViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Message message = messages.get(position);
        if (holder instanceof UserMessageViewHolder) {
            ((UserMessageViewHolder) holder).textViewMessageUser.setText(message.text);
        } else if (holder instanceof BotMessageViewHolder) {
            ((BotMessageViewHolder) holder).textViewMessageBot.setText(message.text);
        }
    }

    static class UserMessageViewHolder extends RecyclerView.ViewHolder {
        TextView textViewMessageUser;

        UserMessageViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewMessageUser = itemView.findViewById(R.id.textViewMessageUser);
        }
    }

    static class BotMessageViewHolder extends RecyclerView.ViewHolder {
        TextView textViewMessageBot;

        BotMessageViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewMessageBot = itemView.findViewById(R.id.textViewMessageBot);
        }
    }
}
