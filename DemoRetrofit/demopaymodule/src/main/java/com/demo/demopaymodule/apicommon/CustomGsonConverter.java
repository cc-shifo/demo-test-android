package com.demo.demopaymodule.apicommon;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.TypeAdapter;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.nio.charset.Charset;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import okio.Buffer;
import retrofit2.Converter;
import retrofit2.Retrofit;


public final class CustomGsonConverter extends Converter.Factory {
    /**
     * Create an instance using a default {@link Gson} instance for conversion. Encoding to JSON and
     * decoding from JSON (when no charset is specified by a header) will use UTF-8.
     */
    public static CustomGsonConverter create() {
        return create(new Gson());
    }

    /**
     * Create an instance using {@code gson} for conversion. Encoding to JSON and decoding from JSON
     * (when no charset is specified by a header) will use UTF-8.
     */
    @SuppressWarnings("ConstantConditions") // Guarding public API nullability.
    public static CustomGsonConverter create(Gson gson) {
        if (gson == null) throw new NullPointerException("gson == null");
        return new CustomGsonConverter(gson);
    }

    private final Gson gson;

    private CustomGsonConverter(Gson gson) {
        this.gson = gson;
    }

    @Override
    public Converter<ResponseBody, ?> responseBodyConverter(
            Type type, Annotation[] annotations, Retrofit retrofit) {
        TypeAdapter<?> adapter = gson.getAdapter(TypeToken.get(type));
        return new ResponseBodyConverter<>(gson, adapter);
    }

    @Override
    public Converter<?, RequestBody> requestBodyConverter(
            Type type,
            Annotation[] parameterAnnotations,
            Annotation[] methodAnnotations,
            Retrofit retrofit) {
        TypeAdapter<?> adapter = gson.getAdapter(TypeToken.get(type));
        return new RequestBodyConverter<>(gson, adapter);
    }

    private static final class RequestBodyConverter<T> implements Converter<T, RequestBody> {
        private final MediaType MEDIA_TYPE = MediaType.get("application/json; charset=UTF-8");
        private final Charset UTF_8 = Charset.forName("UTF-8");

        private final Gson gson;
        private final TypeAdapter<T> adapter;

        RequestBodyConverter(Gson gson, TypeAdapter<T> adapter) {
            this.gson = gson;
            this.adapter = adapter;
        }

        @Override
        public RequestBody convert(T value) throws IOException {
            Buffer buffer = new Buffer();
            Writer writer = new OutputStreamWriter(buffer.outputStream(), UTF_8);
            JsonWriter jsonWriter = gson.newJsonWriter(writer);
            adapter.write(jsonWriter, value);
            jsonWriter.close();
            return RequestBody.create(MEDIA_TYPE, buffer.readByteString());
        }
    }

    private static final class ResponseBodyConverter<T> implements Converter<ResponseBody, T> {
        private final Gson gson;
        private final TypeAdapter<T> adapter;

        ResponseBodyConverter(Gson gson, TypeAdapter<T> adapter) {
            this.gson = gson;
            this.adapter = adapter;
        }

        @Override
        public T convert(ResponseBody value) throws IOException {
            /*Type type = new TypeToken<T>() {}.getType();
            Class<?> rawObservableType = GenericTypeUtils.getRawType(type);

            // Type type2 = getClass().getGenericSuperclass();
            // Type[] types = ((ParameterizedType)type2).getActualTypeArguments();

            Type type3 = new TypeToken<TypeAdapter<T>>() {}.getType();
            Class<?> rawObservableType3 = GenericTypeUtils.getRawType(type3);

            Type type4 = getClass().getGenericSuperclass();
            if(type4 instanceof ParameterizedType){
                Type[] typelist = ((ParameterizedType)type).getActualTypeArguments();
                // Type ty = new ParameterizedTypeImpl(Result.class, new Type[]{types[0]});
                // Result<T> data = gson.fromJson(josndata, ty);
            }
            if(type instanceof ParameterizedType){

                if (type instanceof PaymentRespData) {
                    PaymentRespData resp = gson.fromJson(value.string(), type);
                    if (ServerRespCode.isServerError(resp.getRespCode())) {
                        value.close();
                        throw new ServerRespException(resp.getRespCode(), null);
                    }
                } else if (type instanceof InquiryRespData) {
                    InquiryRespData resp = gson.fromJson(value.string(), type);
                    if (ServerRespCode.isServerError(resp.getRespCode())) {
                        value.close();
                        throw new ServerRespException(resp.getRespCode(), null);
                    }
                }
            }*/


            // String response = value.string();   // value只能读取一次，一次之后就会关闭，所以需要保存
            // try {
            //     JSONObject jsonObject = new JSONObject(response);
            //     String code = jsonObject.getString("code");
            //     String description = jsonObject.getString("description");
            //     handleResponseCode(code);
            // } catch (JSONException e) {
            //     e.printStackTrace();
            // }
            //
            // MediaType contentType = value.contentType();
            // Charset charset = contentType != null ? contentType.charset(StandardCharsets.UTF_8)
            //         : StandardCharsets.UTF_8;
            // InputStream inputStream = new ByteArrayInputStream(response.getBytes());
            // Reader reader = new InputStreamReader(inputStream, charset);
            // JsonReader jsonReader = gson.newJsonReader(reader);


            JsonReader jsonReader = gson.newJsonReader(value.charStream());
            try {
                T result = adapter.read(jsonReader);
                if (jsonReader.peek() != JsonToken.END_DOCUMENT) {
                    throw new JsonIOException("JSON document was not fully consumed.");
                }
                return result;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            } finally {
                value.close();
            }
        }

        private void handleResponseCode(String code) {
            // todo 添加处理逻辑
        }
    }



    public static class ParameterizedTypeImpl implements ParameterizedType {
        private final Class raw;
        private final Type[] args;
        public ParameterizedTypeImpl(Class raw, Type[] args) {
            this.raw = raw;
            this.args = args != null ? args : new Type[0];
        }
        @Override
        public Type[] getActualTypeArguments() {
            return args;
        }
        @Override
        public Type getRawType() {
            return raw;
        }
        @Override
        public Type getOwnerType() {return null;}
    }
}