package com.task.posedetection

import android.app.Activity
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.pose.Pose
import com.google.mlkit.vision.pose.PoseDetection
import com.google.mlkit.vision.pose.PoseDetector
import com.google.mlkit.vision.pose.PoseLandmark
import com.google.mlkit.vision.pose.accurate.AccuratePoseDetectorOptions
import com.task.posedetection.app.AppController
import org.kodein.di.generic.instance

class MainActivity : AppCompatActivity() {
    // region VARIABLES
    private val dialogManager: DialogManager by AppController.kodein().instance()
    private lateinit var poseDetector: PoseDetector
    private var resizedBitmap: Bitmap? = null
    private lateinit var imageBitmap: Bitmap
    // end region VARIABLES


    // region LIFECYCLE methods
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Button click listener
        findViewById<Button>(R.id.btnSelectPicture).setOnClickListener {
            Util.showCameraAndGalleryOption(this, dialogManager)
        }
        findViewById<Button>(R.id.btnPoseDetect).setOnClickListener {
            applyPoseDetection(imageBitmap)
        }

        //Pose detector
        val options = AccuratePoseDetectorOptions.Builder()
            .setDetectorMode(AccuratePoseDetectorOptions.SINGLE_IMAGE_MODE)
            .build()
        poseDetector = PoseDetection.getClient(options)

    }
    //end region LIFECYCLE methods

    // Gallery onActivity result
    var galleryResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                result.data?.data?.let { uri ->
                    findViewById<ImageView>(R.id.ivSelectedPicture).apply {
                        imageBitmap = MediaStore.Images.Media.getBitmap(contentResolver, uri)
                        setImageBitmap(imageBitmap)
                    }
                }
            }
        }

    // Camera onActivity result
    var cameraResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                // There are no request codes
                imageBitmap = result.data?.extras?.get("data") as Bitmap
                findViewById<ImageView>(R.id.ivSelectedPicture).apply {
                    setImageBitmap(imageBitmap)
                }
            }
        }

    private fun applyPoseDetection(imageBitmap: Bitmap) {
        val rotationDegree = 0

        val width: Int = imageBitmap.width
        val height: Int = imageBitmap.height

        resizedBitmap = Bitmap.createBitmap(imageBitmap, 0, 0, width, height)

        val image = InputImage.fromBitmap(resizedBitmap!!, rotationDegree)

        poseDetector.process(image)
            .addOnSuccessListener { pose -> detectPose(pose) }
            .addOnFailureListener {
                Toast.makeText(
                    this,
                    getString(R.string.something_went_wrong),
                    Toast.LENGTH_SHORT
                ).show()
            }
    }


    private fun detectPose(pose: Pose) {
        try {

            //BOTTOM AREA
            val leftKnee = pose.getPoseLandmark(PoseLandmark.LEFT_KNEE)
            val leftAnkle = pose.getPoseLandmark(PoseLandmark.LEFT_ANKLE)

            val rightKnee = pose.getPoseLandmark(PoseLandmark.RIGHT_KNEE)
            val rightAnkle = pose.getPoseLandmark(PoseLandmark.RIGHT_ANKLE)

            //TOP AREA
            val leftShoulder = pose.getPoseLandmark(PoseLandmark.LEFT_SHOULDER)
            val leftHip = pose.getPoseLandmark(PoseLandmark.LEFT_HIP)

            val rightShoulder = pose.getPoseLandmark(PoseLandmark.RIGHT_SHOULDER)
            val rightHip = pose.getPoseLandmark(PoseLandmark.RIGHT_HIP)

            val leftWrist = pose.getPoseLandmark(PoseLandmark.LEFT_WRIST)
            val rightWrist = pose.getPoseLandmark(PoseLandmark.RIGHT_WRIST)

            //SHOUlDER AREA
            val leftShoulderX = leftShoulder!!.position.x
            val leftShoulderY = leftShoulder.position.y
            val rightShoulderX = rightShoulder!!.position.x
            val rightShoulderY = rightShoulder.position.y


            //HIP AREA
            val leftHipX = leftHip!!.position.x
            val leftHipY = leftHip.position.y
            val rightHipX = rightHip!!.position.x
            val rightHipY = rightHip.position.y


            //KNEE AREA
            val leftKneeX = leftKnee!!.position.x
            val leftKneeY = leftKnee.position.y
            val rightKneeX = rightKnee!!.position.x
            val rightKneeY = rightKnee.position.y


            //ANKLE AREA
            val leftAnkleX = leftAnkle!!.position.x
            val leftAnkleY = leftAnkle.position.y
            val rightAnkleX = rightAnkle!!.position.x
            val rightAnkleY = rightAnkle.position.y

            drawPose(
                leftShoulderX,
                leftShoulderY,
                rightShoulderX,
                rightShoulderY,
                leftAnkleX,
                leftAnkleY,
                rightAnkleX,
                rightAnkleY,
                leftKneeX,
                leftKneeY,
                rightKneeX,
                rightKneeY,
                leftHipX,
                leftHipY,
                rightHipX,
                rightHipY,
            )

        } catch (e: Exception) {
            Toast.makeText(this, getString(R.string.something_went_wrong), Toast.LENGTH_LONG).show()
        }

    }

    //Draw Pose
    private fun drawPose(
        leftShoulderX: Float, leftShoulderY: Float, rightShoulderX: Float, rightShoulderY: Float,
        leftAnkleX: Float, leftAnkleY: Float, rightAnkleX: Float, rightAnkleY: Float,
        leftKneeX: Float, leftKneeY: Float, rightKneeX: Float, rightKneeY: Float,
        leftHipX: Float, leftHipY: Float, rightHipX: Float, rightHipY: Float,
    ) {

        val upperAreaColor = Paint()
        val lowerAreaColor = Paint()


        //Get color
        val upperAreaColorList = mutableListOf<Int>()
        upperAreaColorList.add(
            resizedBitmap!!.getPixel(
                rightShoulderX.toInt(),
                rightShoulderY.toInt()
            )
        )
        upperAreaColorList.add(resizedBitmap!!.getPixel(rightHipX.toInt(), rightHipY.toInt()))
        upperAreaColorList.add(resizedBitmap!!.getPixel(leftHipX.toInt(), leftHipY.toInt()))
        upperAreaColorList.add(
            resizedBitmap!!.getPixel(
                leftShoulderX.toInt(),
                leftShoulderY.toInt()
            )
        )


        val upperColor = upperAreaColorList.averageColor()
        upperAreaColor.color = upperColor
        upperAreaColor.strokeWidth = 4f

        //Get color
        val lowerAreaColorList = mutableListOf<Int>()
        lowerAreaColorList.add(resizedBitmap!!.getPixel(rightKneeX.toInt(), rightKneeY.toInt()))
        lowerAreaColorList.add(resizedBitmap!!.getPixel(rightAnkleX.toInt(), rightAnkleY.toInt()))
        lowerAreaColorList.add(resizedBitmap!!.getPixel(leftKneeX.toInt(), leftKneeY.toInt()))
        lowerAreaColorList.add(resizedBitmap!!.getPixel(leftAnkleX.toInt(), leftAnkleY.toInt()))


        val lowerColor = lowerAreaColorList.averageColor()
        lowerAreaColor.color = lowerColor
        lowerAreaColor.strokeWidth = 4f


        val drawBitmap = Bitmap.createBitmap(
            resizedBitmap!!.width,
            resizedBitmap!!.height,
            resizedBitmap!!.config
        )

        val canvas = Canvas(drawBitmap)

        //load original image into the canvas
        canvas.drawBitmap(resizedBitmap!!, 0f, 0f, null)


        //Draw upper area
        canvas.drawLine(
            leftShoulderX,
            leftShoulderY,
            rightShoulderX,
            rightShoulderY,
            upperAreaColor
        )
        canvas.drawLine(rightShoulderX, rightShoulderY, rightHipX, rightHipY, upperAreaColor)
        canvas.drawLine(rightHipX, rightHipY, leftHipX, leftHipY, upperAreaColor)
        canvas.drawLine(leftHipX, leftHipY, leftShoulderX, leftShoulderY, upperAreaColor)
        canvas.drawLine(rightHipX, rightHipY, leftHipX, leftHipY, upperAreaColor)

        //Draw lower area
        canvas.drawLine(rightHipX, rightHipY, rightKneeX, rightKneeY, lowerAreaColor)
        canvas.drawLine(rightKneeX, rightKneeY, rightAnkleX, rightAnkleY, lowerAreaColor)

        canvas.drawLine(leftHipX, leftHipY, leftKneeX, leftKneeY, lowerAreaColor)
        canvas.drawLine(leftKneeX, leftKneeY, leftAnkleX, leftAnkleY, lowerAreaColor)

        canvas.drawLine(leftAnkleX, leftAnkleY, rightAnkleX, rightAnkleY, lowerAreaColor)

        findViewById<ImageView>(R.id.ivSelectedPicture).setImageBitmap(drawBitmap)

    }
}