package edu.temple.editorfragment

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.AssetManager
import android.content.res.Resources
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.Button
import android.widget.RelativeLayout
import androidx.appcompat.content.res.AppCompatResources
import com.google.android.material.button.MaterialButton
import org.json.JSONObject

class DynamicHelper {
    fun generateButtons(view: View, context: Context): MutableList<Button> {
        val jsonString = getRemoteFile("remote_ui.json", context)
        val jsonObject = JSONObject(jsonString)
        val jsonArray = jsonObject.getJSONArray("buttons")
        // convert to buttonDTO
        val buttonDTOList: MutableList<ButtonDTO> = ArrayList()
        for (i in 1..jsonArray.length()) {
            val json = jsonArray.getJSONObject(i - 1)
            val buttonDTO = ButtonDTO(
                json.getString("background_color"),
                json.getString("size"),
                json.getDouble("top_position_percent"),
                json.getDouble("left_position_percent"),
                json.getString("display_name"),
                json.getString("code")
            )
            buttonDTOList.add(buttonDTO)
        }
        // create the ui
        val layout: RelativeLayout = view.findViewById(R.id.relative_layout)
        val buttonList: MutableList<Button> = ArrayList()


        val width = Resources.getSystem().displayMetrics.widthPixels
        val height = Resources.getSystem().displayMetrics.heightPixels

        for (buttonDTO in buttonDTOList) {
            val button = MaterialButton(context)
            button.width = 75
            button.height = 150
            button.cornerRadius = 50
            button.iconSize =  75
            button.backgroundTintList = context.getColorStateList(androidx.appcompat.R.color.button_material_light)

            when (buttonDTO.displayName) {
                "PresetUp" -> {
                    button.icon = AppCompatResources.getDrawable(context,R.drawable.ic_skip_next)
                }
                "Off" -> {
                    button.icon = AppCompatResources.getDrawable(context, R.drawable.ic_power)
                }
                "Up" -> {
                    button.icon = AppCompatResources.getDrawable(context, R.drawable.ic_arrow_up)
                }
                "Down" -> {
                    button.icon = AppCompatResources.getDrawable(context, R.drawable.ic_arrow_down)
                }
                "presetDown" -> {
                    button.icon = AppCompatResources.getDrawable(context, R.drawable.ic_skip_previous)
                }
                "VolUp" -> {
                    button.icon = AppCompatResources.getDrawable(context, R.drawable.ic_volume_up)
                }
                "ChannelUp" -> {
                    button.icon = AppCompatResources.getDrawable(context, R.drawable.ic_add)
                }
                "VolDown" -> {
                    button.icon = AppCompatResources.getDrawable(context, R.drawable.ic_volume_down)
                }
                "ChannelDown" -> {
                    button.icon = AppCompatResources.getDrawable(context, R.drawable.ic_remove)
                }
                else -> {
                    button.text = buttonDTO.displayName
                }
            }

            Log.d("AAA", "${button.width}")

            val layoutParam = RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT
            )
            layoutParam.leftMargin = (buttonDTO.leftPositionPercent * width).toInt() - 100
            layoutParam.topMargin = (buttonDTO.topPositionPercent * height).toInt()

            button.layoutParams = layoutParam
            buttonList.add(button)
            button.setOnTouchListener(object : View.OnTouchListener {

                @SuppressLint("ClickableViewAccessibility")
                override fun onTouch(view: View?, event: MotionEvent?): Boolean {
                    if (event?.action== MotionEvent.ACTION_MOVE) {
                        val layoutParam = RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT)
                        layoutParam.leftMargin = event.rawX.toInt() - (view!!.width / 2);
                        layoutParam.topMargin = event.rawY.toInt() - (view.height / 2) - 180
                        view.layoutParams = layoutParam
                    }
                    return true
                }

            })
            layout.addView(button)

        }
        return buttonList
    }
    fun saveLayout(buttonList: MutableList<Button>, context: Context){

    }


    private fun getRemoteFile(filename: String, context: Context): String {
        val manager : AssetManager = context.assets
        val file = manager.open(filename)
        val bytes = ByteArray(file.available())
        file.read(bytes)
        file.close()
        return String(bytes)
    }

}