package com.example.tomipc.foodzye.fragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tomipc.foodzye.Food;
import com.example.tomipc.foodzye.FoodAdapter;
import com.example.tomipc.foodzye.R;
import com.example.tomipc.foodzye.loginActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link AddFoodFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link AddFoodFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AddFoodFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    //private OnFragmentInteractionListener mListener;

    private static final String FOOD_URL = "http://164.132.228.255/food";

    HttpURLConnection connection;
    AutoCompleteTextView ACText;
    Button addNewFood, CapturePicture;
    ProgressDialog progressDialog;
    ImageView imgPreview;
    ArrayList<Food> arrayOfFood;
    String foodJSON;
    String foodImage;
    Food chosenFood;
    private String encoded_string, image_name;
    private String filePath = null;
    private Bitmap bitmap;
    private File file;
    private Uri file_uri;

    public AddFoodFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AddFoodFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AddFoodFragment newInstance(String param1, String param2) {
        AddFoodFragment fragment = new AddFoodFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        super.onCreate(savedInstanceState);

        // Construct the data source
        arrayOfFood = new ArrayList<Food>();
        try {
            foodJSON = new getFoodJSON().execute(FOOD_URL).get();
            JSONArray obj = new JSONArray(foodJSON);
            for (int i = 0; i < obj.length(); i++) {
                JSONObject jObject = obj.getJSONObject(i);
                int id = jObject.getInt("id");
                String name = jObject.getString("name");
                Food food = new Food(id, name);
                arrayOfFood.add(food);
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View x = inflater.inflate(R.layout.activity_add_food, container, false);

        ACText = (AutoCompleteTextView) x.findViewById(R.id.acText);
        addNewFood = (Button) x.findViewById(R.id.AddFoodButton2);
        CapturePicture = (Button) x.findViewById(R.id.take_picture);
        progressDialog = new ProgressDialog(getActivity(), R.style.AppTheme_Dark_Dialog);
        imgPreview = (ImageView) x.findViewById(R.id.imgPreview);

        CapturePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                file_uri = Uri.fromFile(getOutputMediaFile());
                i.putExtra(MediaStore.EXTRA_OUTPUT, file_uri);
                startActivityForResult(i, 10);
            }
        });

        // Create the adapter to convert the array to views
        FoodAdapter adapter = new FoodAdapter(getActivity(), R.layout.item_food2, arrayOfFood);
        // Attach the adapter to a AutoCompleteTextView
        ACText.setAdapter(adapter);

        //OnItemClickListener for the AutoCompleteTextView
        ACText.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long rowId) {
                chosenFood = (Food) parent.getItemAtPosition(position);
                if(chosenFood.name.equals("There is no such food. Click me if you want to add it.")){
                    addNewFood.setVisibility(View.VISIBLE);
                }else{
                    Toast.makeText(getContext(),chosenFood.name, Toast.LENGTH_LONG).show();
                }
            }
        });

        // Inflate the layout for this fragment
        return x;
    }

    // TODO: Rename method, update argument and hook method into UI event
    /*public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }*/

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        /*if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }*/
    }

    @Override
    public void onDetach() {
        super.onDetach();
        //mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    /*public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }*/

    private class getFoodJSON extends AsyncTask<String, String, String> {

        protected String doInBackground(String... urls) {
            try {
                URL url = null;
                String response = null;
                url = new URL(FOOD_URL);
                //create the connection
                connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.setDoOutput(false);
                //set the request method to GET
                connection.setRequestMethod("GET");
                String line = "";
                //create your inputstream
                InputStreamReader in = new InputStreamReader(connection.getInputStream());
                //read in the data from input stream
                BufferedReader reader = new BufferedReader(in);
                StringBuilder sb = new StringBuilder();
                while ((line = reader.readLine()) != null) {
                    sb.append(line + "\n");
                }
                //get the string version of the response data
                response = sb.toString();
                //close input streams
                in.close();
                reader.close();
                return response;
            } catch (Exception e) {
                Log.e("HTTP GET:", e.toString());
            }
            return "Error. Please try again later.";
        }
    }

    private File getOutputMediaFile() {

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",
                Locale.getDefault()).format(new Date());

        foodImage = "IMG_" + timeStamp + ".jpg";


        // External sdcard location
        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + File.separator + foodImage);

        // Create the storage directory if it does not exist
        /*if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d("Error", "Oops! Failed create "
                        + "Foodzye" + " directory");
                return null;
            }
        }*/

        // Create a media file name
        /*String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",
                Locale.getDefault()).format(new Date());
        file = new File(mediaStorageDir.getPath() + File.separator + "IMG_" + timeStamp + ".jpg");*/


        return file;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 10 && resultCode == Activity.RESULT_OK) {
            new Upload_image().execute("http://164.132.228.255/food_image");
        }
    }

    private void previewMedia() {
        imgPreview.setVisibility(View.VISIBLE);
        // bitmap factory
        BitmapFactory.Options options = new BitmapFactory.Options();

        // down sizing image as it throws OutOfMemory Exception for larger images
        options.inSampleSize = 8;

        final Bitmap bitmap = BitmapFactory.decodeFile(filePath, options);

        imgPreview.setImageBitmap(bitmap);
    }

    private class Upload_image extends AsyncTask<String, Integer, String> {

        @Override
        protected void onPreExecute() {
            progressDialog.setIndeterminate(true);
            progressDialog.setMessage("Uploading the image...");
            progressDialog.show();
            super.onPreExecute();
        }

        private String getPostDataString(HashMap<String, String> params) throws UnsupportedEncodingException {
            StringBuilder result = new StringBuilder();
            boolean first = true;
            for (Map.Entry<String, String> entry : params.entrySet()) {
                if (first)
                    first = false;
                else
                    result.append("&");

                result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
                result.append("=");
                result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
            }

            return result.toString();
        }

        @Override
        protected String doInBackground(String... params) {

            bitmap = BitmapFactory.decodeFile(file_uri.getPath());
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);

            byte[] imageBytes = stream.toByteArray();
            encoded_string = Base64.encodeToString(imageBytes, Base64.DEFAULT);

            HashMap<String,String> data = new HashMap<>();
            data.put("encoded_string", encoded_string);
            //data.put("image_name", foodImage);

            URL url;
            String response = "";
            try {
                url = new URL(params[0]);

                Log.d("Debug", "URL je " + url);
                Log.d("Debug", "Podaci su " + data.get("encoded_string"));

                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(15000);
                conn.setConnectTimeout(15000);
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.setDoOutput(true);


                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(os, "UTF-8"));
                writer.write(getPostDataString(data));

                writer.flush();
                writer.close();
                os.close();

                Log.d("Debug", "poslano");

                int responseCode = conn.getResponseCode();

                Log.d("Debug", "Response code je " + responseCode);

                if (responseCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    response = br.readLine();
                } else {
                    response = "Error";
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            return response;

        }

        @Override
        protected void onPostExecute(String result) {
            Log.e("Debug", "Response from server: " + result);
            progressDialog.dismiss();
            //previewMedia();
            super.onPostExecute(result);
        }

    }

}
