package nascimento.santos.matheus.galeria3;

import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class MainAdapter extends RecyclerView.Adapter {

    MainActivity mainActivity;
    List<String> photos;

    public MainAdapter(MainActivity mainActivity, List<String> photos) {
        this.mainActivity = mainActivity;
        this.photos = photos;//guarda o mainactivity e a lista das fotos
    }

    public MainAdapter(View v) {
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mainActivity);
        View v = inflater.inflate(R.layout.list_item, parent, false);
        return new ViewHolder(v);
    }//cria o item com os elementos

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {
        ImageView imPhoto = holder.itemView.findViewById(R.id.imItem);
        int w = (int) mainActivity.getResources().getDimension(R.dimen.itemWidth);
        int h = (int) mainActivity.getResources().getDimension(R.dimen.itemHeight);//pega a imageviewm do main e define o tamanho
        Bitmap bitmap = Util.getBitmap(photos.get(position), w, h);//pega a imagem em bitmap e deixa no tamanho do imageview
        imPhoto.setImageBitmap(bitmap);//coloca o bitmap no imageview
        imPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainActivity.startPhotoActivity(photos.get(position));
            }
        });//ao clicar na imagem abre o photoactivity
    }

    @Override
    public int getItemCount() {//manda o tamanho da lista para o recycleview
        return photos.size();
    }//manda o tamanho da lista para o recycleview
}
