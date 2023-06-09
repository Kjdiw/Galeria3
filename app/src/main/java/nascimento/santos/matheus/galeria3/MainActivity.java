package nascimento.santos.matheus.galeria3;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import androidx.core.content.PackageManagerCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.GridLayout;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import android.Manifest;

public class MainActivity extends AppCompatActivity {

    static int RESULT_TAKE_PICTURE = 1;

    static int RESULT_REQUEST_PERMISSION = 2;

    String currentPhotoPath;
    List<String> photos = new ArrayList<>();
    MainAdapter mainAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        File dir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);//acessa o diretório
        File[] files = dir.listFiles();
        for(int i = 0; i< files.length; i++) {
            photos.add(files[i].getAbsolutePath());
        }//abre a lista de fotos salvas e adiciona a nova foto

        mainAdapter = new MainAdapter(MainActivity.this, photos);//cria o mainadapter


        List<String> permissions = new ArrayList<>();
        permissions.add(Manifest.permission.CAMERA);

        checkForPermissions(permissions);//cria uma lista com as permissões pedidas, adiciona a câmera a essa lista e verifica as permissões

        RecyclerView rvGallery = findViewById(R.id.rvGallery);
        rvGallery.setAdapter(mainAdapter);//bota o mainadapter no recycleview

        float w = getResources().getDimension(R.dimen.itemWidth);
        int numberOfColumns = Util.calculateNoOfColumns(MainActivity.this, w);//calcula a quantidade de colunas no celular
        GridLayoutManager gridLayoutManager = new GridLayoutManager(MainActivity.this, numberOfColumns);
        rvGallery.setLayoutManager(gridLayoutManager);//define o recycleview para respeitar a quantidade de colunas que cabem no celular e define display grid

        Toolbar toolbar = findViewById(R.id.tbMain);
        setSupportActionBar(toolbar);//pega a toolbar e define como interativa
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_activity_tb, menu);
        return true;//faz o xml menu ficar em cima
    }

    public void startPhotoActivity(String photoPath) {
        Intent i = new Intent(MainActivity.this, PhotoActivity.class);
        i.putExtra("photo_path", photoPath);
        startActivity(i);//passa o caminho da foto para photo activity
    }

    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.opShare:
                dispatchTakePictureIntent();
                return true;
            default:
                return super.onOptionsItemSelected(item);//ao clicar no ícone aciona a câmera do celular
        }
    }

    private void dispatchTakePictureIntent() {
        File f = null;
        try {
            f = createImageFile();
        } catch (IOException e) {
            Toast.makeText(MainActivity.this, "Não foi possível criar o arquivo", Toast.LENGTH_LONG).show();
            return;
        }//tenta criar um arquivo vazio para guardar a imagem dentro da pasta de imagens

        currentPhotoPath = f.getAbsolutePath();//cria o local do arquivo

        if(f != null) {
            Uri fUri = FileProvider.getUriForFile(MainActivity.this, "nascimento.santos.matheus.galeria3.fileprovider", f);
            Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            i.putExtra(MediaStore.EXTRA_OUTPUT, fUri);
            startActivityForResult(i, RESULT_TAKE_PICTURE);
        }
    }//passa a uri da foto e inicia a camera

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp;
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File f = File.createTempFile(imageFileName, ".jpg", storageDir);
        return f;
    }//cria o arquivo com nome da data e hora da criação

    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == RESULT_TAKE_PICTURE) {
            if(resultCode == Activity.RESULT_OK) {
                photos.add(currentPhotoPath);
                mainAdapter.notifyItemInserted(photos.size()-1);
            }//ve se a foto foi tirada, adiciona na lista de fotos e avisa o main adapter disso para atualizar o recycle view
            else {
                File f = new File(currentPhotoPath);
                f.delete();
            }//se a foto não foi tirada é excluído o arquivo em que ela iria ficar
        }
    }

    private void checkForPermissions(List<String> permissions) {
        List<String> permissionsNotGranted = new ArrayList<>();

        for(String permission : permissions) {
            if (!hasPermission(permission)) {
                permissionsNotGranted.add(permission);
            }//cria uma lista de permissões, le elas e adiciona a uma lista de permissões não dadas se for o caso
        }
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if(permissionsNotGranted.size()>0) {
                requestPermissions(permissionsNotGranted.toArray(new String[permissionsNotGranted.size()]), RESULT_REQUEST_PERMISSION);
            }//pede a permissão que estiver na lista de permissões não dadas
        }
    }

    private boolean hasPermission(String permission) {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return ActivityCompat.checkSelfPermission(MainActivity.this, permission) == PackageManager.PERMISSION_GRANTED;
        }
        return false;
    }//verifica se a permissão já foi dada ou não

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        final List<String> permissionsRejected = new ArrayList<>();
        if(requestCode == RESULT_REQUEST_PERMISSION) {
            for(String permission : permissions) {
                if(!hasPermission(permission)) {
                    permissionsRejected.add(permission);
                }
            }
        }//verifica as permissões após negar ou aceitar ela
        if(permissionsRejected.size() > 0) {
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if(shouldShowRequestPermissionRationale(permissionsRejected.get(0))) {
                    new AlertDialog.Builder(MainActivity.this).setMessage("Para usar essa app é preciso conceder essas permissões").setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            requestPermissions(permissionsRejected.toArray(new String[permissionsRejected.size()]), RESULT_REQUEST_PERMISSION);
                        }
                    } ).create().show();
                }//vê se alguma permissão neessária para o aplicativo foi negada, informa isso ao usuário e pede permissão a ele de novo
            }
        }
    }
}