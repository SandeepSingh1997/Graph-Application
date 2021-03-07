package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.RectF;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    int choice=1 ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final GraphView graphView= new GraphView(this);
        setContentView(R.layout.activity_main);
        ViewGroup viewGroup = findViewById(R.id.graphcontainer);
        viewGroup.addView(graphView);
    }

    public class MyPoint{
        public float x;
        public float y ;
    }


    public class GraphView extends View{

        final int grid_unit=100;

        float zoom = 1.0f ;

        float unitX = 0.1f;
        int pixUnitX =100;//pixUnitX pixels - unitX value
        float unitMapX=pixUnitX*zoom;

        int nofpts=100;

        float unitY = 1f;
        int pixUnitY =100;
        float unitMapY=pixUnitY*zoom;

        public GraphView(Context context) {
            super(context);
        }
        @Override
        protected void onDraw(Canvas canvas){
            super.onDraw(canvas);
            Paint paint = new Paint();
            paint.setStyle(Paint.Style.FILL);
            paint.setAntiAlias(true);
            paint.setColor(Color.BLACK);
            drawGraph(canvas,paint);
            }
            public float expression(float x){
            float y=0;
            try {
                switch (choice){
                    case 0 : y=(float) Math.pow(Math.sin(x),2) ;
                        break;
                    case 1 : y=(float) Math.sin(x*x) ;
                        break;
                    case 2 : y=(float) Math.tan(x) ;
                        break;
                    case 3 : y=(float) Math.pow(x,2) ;
                        break;
                    default: y= 0 ;
                }
            }catch (Exception e){
            }
                return y;
            }

        public ArrayList<MyPoint> getGraphPoints(){
            int noInterpts=10;//no of interpolating points
            int Npts=20;//points between each noInterpts

            nofpts=Npts*((int) (getWidth()/(pixUnitX)) );//multiply to increase graph length
            float x=0;
            int cnt=(int)(-nofpts)/(Npts*2)*noInterpts;
            ArrayList<MyPoint> points = new ArrayList<>();
            float[][] Inter = new float[noInterpts][2] ;

            for(int k=0; k<nofpts; k+=Npts){

                for (int i=0 ;i<noInterpts; i++){
                    x=cnt*(unitX/zoom);
                    Inter[i][0] =x*unitMapX;
                    Inter[i][1] = (float)( expression(x) )*unitMapY;
                    cnt++;
                }
                float[][] point=Interpolation(Inter,Npts);
                for (int i=0 ;i<Npts; i++){
                    MyPoint point1= new MyPoint();
                    point1.x = point[i][0];
                    point1.y = point[i][1];
                    points.add(point1);
                }
            }
            return points ;
        }

            public void drawAxis(Canvas canvas, Paint paint){
                float h=getHeight();
                float w=getWidth();
                paint.setColor(Color.BLACK);
                canvas.drawLine(0,h/2, w,h/2,paint);//xaxis
                canvas.drawLine(w/2,0,w/2,h,paint);//yaxis
                paint.setColor(Color.GRAY);
                paint.setTextSize(25);
                canvas.drawText("0",w/2+10,h/2-10,paint);

                for(int i=1 ; grid_unit*i<(h/2) || grid_unit*i<(w/2); i++){
                canvas.drawLine(0,h/2+grid_unit*i, w,h/2+grid_unit*i,paint);//lower xaxis
                canvas.drawText(String.format("%.2f",-(unitY*grid_unit)/unitMapY*i),w/2+10,h/2+grid_unit*i-10,paint);

                canvas.drawLine(w/2+grid_unit*i,0,w/2+grid_unit*i,h,paint);//right yaxis
                canvas.drawText(String.format("%.2f",(unitY*grid_unit)/unitMapY*i),w/2+grid_unit*i+10,h/2-10,paint);

                canvas.drawLine(0,h/2-grid_unit*i, w,h/2-grid_unit*i,paint);//xaxis
                canvas.drawText(String.format("%.2f",(unitY*grid_unit)/unitMapY*i),w/2+10,h/2-grid_unit*i-10,paint);

                canvas.drawLine(w/2-grid_unit*i,0,w/2-grid_unit*i,h,paint);//yaxis
                canvas.drawText(String.format("%.2f",-(unitY*grid_unit)/unitMapY*i),w/2-grid_unit*i+10,h/2-10,paint);
            }
                paint.setColor(Color.BLACK);
            }

        public void drawGraph(Canvas canvas, Paint paint){
            drawAxis(canvas,paint);
            paint.setStrokeWidth(3);
            ArrayList<MyPoint> points = getGraphPoints();
            float scrwidth = getWidth();
            float scrheight = getHeight();
            //canvas.drawText(""+points.size(),100,100,paint);
            for(int i=0 ; i<points.size()-1; i++){
                canvas.drawLine((scrwidth/2)+points.get(i).x, (scrheight/2)-points.get(i).y,(scrwidth/2)+points.get(i+1).x, (scrheight/2)-points.get(i+1).y,paint);
                //canvas.drawPoint((scrwidth/2)+points.get(i).x, (scrheight/2)-points.get(i).y,paint);

            }
        }

        public  float[][] Interpolation(float Points[][],int noPts){
            float output[][]=new float[noPts][2] ;
            int Pointslen=Points.length ;
            float lp = 1,result=0,input=0,diff=(Points[Pointslen-1][0]-Points[0][0])/noPts;
            for(int k=0; k<noPts; k++){
                input=Points[0][0]+(k*diff);
                result=0;
            for(int i=0; i<Pointslen;i++)
            {
                lp=1;
                for(int j=0; j<Pointslen;j++){
                    if(!(i==j)){
                        lp*=(input-Points[j][0])/(Points[i][0]-Points[j][0]);
                    }
                }
                lp*=Points[i][1];
                result+=lp;
            }
                output[k][0]=input;
             output[k][1]=result;
        }
            return output ;
        }

/*
        public ArrayList<MyPoint> getGraphPoints(){
            nofpts = (int) ((getWidth()/unitMapX)*zoom) ;
            ArrayList<MyPoint> points = new ArrayList<>();
            float X = 0 ;
            float Y = 0 ;
            int cnt = 0 ;
            int x = -nofpts/2 ;

             while(cnt<nofpts){
                MyPoint point = new MyPoint();
                X=x*unitX;//x value
                point.x=X*unitMapRatioX;//this exp means actual value of x is (x*unitX) and it will be seen as unitmapped value
                try {
                    Y=(float)(Math.sin(X*X)) ;//Write the function here
                    point.y=Y*unitMapRatioY;
                }catch (Exception e){
                    point.y=(float)0;
                }
                points.add(point);
                x++;
                cnt++;
            }
            return points ;
        }

            public void drawGraph(Canvas canvas, Paint paint){
                drawAxis(canvas,paint);
                paint.setStrokeWidth(3);
                ArrayList<MyPoint> points = getGraphPoints();
                float scrwidth = getWidth();
               float scrheight = getHeight();
               for(int i=0 ; i<points.size()-1; i++){
                   canvas.drawLine((scrwidth/2)+points.get(i).x, (scrheight/2)-points.get(i).y,(scrwidth/2)+points.get(i+1).x, (scrheight/2)-points.get(i+1).y,paint);
               }}
            */

    }

}
