import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class MovieArrayAdapter extends ArrayAdapter<Movie> {
  private ImageView movieImage;
	private TextView movieName;
	private TextView rating;
	private List<Movie> movies = new ArrayList<Movie>();

	public MovieArrayAdapter(Context context, int textViewResourceId,
			List<Movie> objects) {
		super(context, textViewResourceId, objects);
		this.movies = objects;
	}

	public int getCount() {
		return this.movies.size();
	}

	public Movie getItem(int index) {
		return this.movies.get(index);
	}

	// REFERENCE: this method is modified from
	// "Android ListView with icons/images"
	// http://w2davids.wordpress.com/android-listview-with-iconsimages-and-sharks-with-lasers/
	public View getView(int position, View convertView, ViewGroup parent) {
		View row = convertView;
		if (row == null) {
			LayoutInflater inflater = (LayoutInflater) this.getContext()
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			row = inflater.inflate(R.layout.list_item, parent, false);
		}

		// get items
		Movie movie = getItem(position);
		movieImage = (ImageView) row.findViewById(R.id.image);
		movieName = (TextView) row.findViewById(R.id.title);
		rating = (TextView) row.findViewById(R.id.rating);

		// set the content of the items
		movieImage.setImageBitmap(getBitmap(movie.imageURL));
		movieName.setText(movie.title);
		rating.setText(movie.rating);
		return row;
	}
	
	// REFERENCE: this method is modified from 
	// "Android Development Tutorial: Asynchronous Lazy Loading and Caching of ListView Images"
	// http://codehenge.net/blog/2011/06/android-development-tutorial-asynchronous-lazy-loading-and-caching-of-listview-images/
	static public Bitmap getBitmap(String bitmapUrl) {
		try 
		{
			URL url = new URL(bitmapUrl);
			return BitmapFactory.decodeStream(url.openConnection().getInputStream());
		}
		catch(MalformedURLException e) 
		{
			e.printStackTrace();
			return null;
		}
		catch(IOException e)
		{
			e.printStackTrace();
			return null;
		}
			
		}
}
