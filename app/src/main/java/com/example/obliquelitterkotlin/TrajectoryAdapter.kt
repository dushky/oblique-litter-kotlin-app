import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.obliquelitterkotlin.TrajectoryPoint
import com.example.obliquelitterkotlin.databinding.TrajectoryPointBinding

class TrajectoryAdapter(private val points: List<TrajectoryPoint>) :
    RecyclerView.Adapter<TrajectoryAdapter.ViewHolder>() {

    inner class ViewHolder(private val binding: TrajectoryPointBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(point: TrajectoryPoint) {
            binding.timeTextView.text = String.format("%.2f s", point.time)
            binding.xTextView.text = String.format("%.2f m", point.x)
            binding.yTextView.text = String.format("%.2f m", point.y)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = TrajectoryPointBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(points[position])
    }

    override fun getItemCount(): Int = points.size
}