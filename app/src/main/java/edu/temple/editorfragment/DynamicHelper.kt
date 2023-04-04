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
import org.json.JSONArray
import org.json.JSONObject

class DynamicHelper {
    fun generateButtons(view: View, context: Context): MutableMap<Button,String> {
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
        val buttonMap: MutableMap<Button,String> = mutableMapOf()


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
            layoutParam.topMargin = (buttonDTO.topPositionPercent * height).toInt() + 200

            button.layoutParams = layoutParam
            buttonMap[button] = buttonDTO.code
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
        return buttonMap
    }
    fun saveLayout(buttonMap: MutableMap<Button,String>, context: Context){
        val width = Resources.getSystem().displayMetrics.widthPixels
        val height = Resources.getSystem().displayMetrics.heightPixels

        val background = "white"
        val size = "normal"
        val jsonArray = JSONArray()
        val buttonDTOlist: MutableList<ButtonDTO> = ArrayList()
        for(button in buttonMap){
            val topPosPercent = button.component1().top / height
            val leftPosPercent = button.component1().left / width

            lateinit var buttonName: String
            when ((button.component1()as MaterialButton).icon) {
                AppCompatResources.getDrawable(context,R.drawable.ic_skip_next) -> {
                    buttonName = "PresetUp"
                }
                AppCompatResources.getDrawable(context, R.drawable.ic_power) -> {
                    buttonName = "Off"
                }
                AppCompatResources.getDrawable(context, R.drawable.ic_arrow_up) -> {
                    buttonName = "Up"
                }
                AppCompatResources.getDrawable(context, R.drawable.ic_arrow_down) -> {
                    buttonName ="Down"
                }
                AppCompatResources.getDrawable(context, R.drawable.ic_skip_previous) -> {
                    buttonName = "presetDown"
                }
                AppCompatResources.getDrawable(context, R.drawable.ic_volume_up) -> {
                    buttonName = "VolUp"
                }
                AppCompatResources.getDrawable(context, R.drawable.ic_add) -> {
                    buttonName = "ChannelUp"
                }
                AppCompatResources.getDrawable(context, R.drawable.ic_volume_down) -> {
                    buttonName = "VolDown"
                }
                AppCompatResources.getDrawable(context, R.drawable.ic_remove) -> {
                    buttonName = "ChannelDown"
                }
                else -> {
                    buttonName = button.component1().text.toString()
                }
            }

            val json = JSONObject()
            json.put("background_color", background)
            json.put("size", size)
            json.put("top_position_percent", topPosPercent)
            json.put("left_position_percent", leftPosPercent)
            json.put("display_name", buttonName)
            json.put("code", button.component2())

            jsonArray.put(json)
        }
        val outJSON = JSONObject()
        outJSON.put("buttons", jsonArray)
        saveToRemoteFile("remote_ui.json", context, outJSON.toString())
    }
    private fun saveToRemoteFile(filename: String, context: Context, json: String ){
        val manager : AssetManager = context.assets
        val file = manager.open(filename)
        //TODO
    }


    private fun getRemoteFile(filename: String, context: Context): String {
        val manager : AssetManager = context.assets
        val file = manager.open(filename)
        val bytes = ByteArray(file.available())
        file.read(bytes)
        file.close()
        return String(bytes)
        //TODO (rework this, we can't use assets)
    }

}