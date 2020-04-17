package sivachenko.m.l33;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.Arrays;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    final int MENU_RESET_ID = 1;
    final int MENU_QUIT_ID = 2;

    EditText etA;
    EditText etB;
    EditText etC;
    EditText etD;
    EditText etY;

    Button btCount;

    static TextView tvResult;

    static int[] delta = new int[5];
    static int[] rndSolvesRes = new int[5];

    static int[][] pairOfParents = new int[5][2];
    static int[][] parents = new int[5][4];
    static int[][] children = new int[5][4];
    static int[][] rndSolves = new int[5][4];

    static double[] probabilityOfChoosingChromosome = new double[5];
    static double sumOfInverseCoefficients = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // находим элементы
        etA = (EditText) findViewById(R.id.a);
        etB = (EditText) findViewById(R.id.b);
        etC = (EditText) findViewById(R.id.c);
        etD = (EditText) findViewById(R.id.d);
        etY = (EditText) findViewById(R.id.y);

        btCount = (Button) findViewById(R.id.count);

        tvResult = (TextView) findViewById(R.id.results);

        btCount.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {

        int a = 0;
        int b = 0;
        int c = 0;
        int d = 0;
        int y = 0;

        if (TextUtils.isEmpty(etA.getText().toString())
                || TextUtils.isEmpty(etB.getText().toString())
                || TextUtils.isEmpty(etC.getText().toString())
                || TextUtils.isEmpty(etD.getText().toString())
                || TextUtils.isEmpty(etY.getText().toString())) {
            return;
        }

        a = Integer.parseInt(etA.getText().toString());
        b = Integer.parseInt(etB.getText().toString());
        c = Integer.parseInt(etC.getText().toString());
        d = Integer.parseInt(etD.getText().toString());
        y = Integer.parseInt(etY.getText().toString());


        switch (v.getId()) {
            case R.id.count:
                    solving(a, b, c, d, y);
                break;
            default:
                break;
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0, MENU_RESET_ID, 0, "Reset");
        menu.add(0, MENU_QUIT_ID, 0, "Quit");
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case MENU_RESET_ID:
                etA.setText("");
                etB.setText("");
                etC.setText("");
                etD.setText("");
                etY.setText("");

                tvResult.setText("");
                break;
            case MENU_QUIT_ID:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public static void solving(int a, int b, int c, int d, int y) {
        int iterNum = 0;
        for (int i = 0; i < rndSolves.length; i++) {
            for (int j = 0; j < rndSolves[i].length; j++) {
                rndSolves[i][j] = 1 + (int) (Math.random() * y/2);
            }
        }
        finding: {
            do {
                findRndSolveRes(rndSolves, a, b, c, d, y);
                for (int i = 0; i < rndSolvesRes.length; i++) {
                    if(rndSolvesRes[i] == y){
                        tvResult.setText("x1 = " + rndSolves[i][0] + ", x2 = " + rndSolves[i][1] + ", x3 = " + rndSolves[i][2] + ", x4 = " + rndSolves[i][3]);
                        break finding;
                    }
                }
                findDelta(rndSolvesRes, y);
                findSumOfInverseCoefficients(delta);
                findProbabilityOfChoosingChromosome(delta);
                findPairOfParents();
                findChildren(pairOfParents);
                mutation(y);
                iterNum++;
            } while( iterNum < 10000);
        }

        if(iterNum >=10000){
            tvResult.setText("Solving was not found.");
        }

    }

    public static void findRndSolveRes(int[][] rndSolves, int a, int b, int c, int d, int y) {
        for (int i = 0; i < rndSolvesRes.length; i++) {
            rndSolvesRes[i] = a * rndSolves[i][0] + b * rndSolves[i][1] + c * rndSolves[i][2] + d * rndSolves[i][3];
        }
    }

    public static void findDelta(int[] someRes, int y) {
        for (int i = 0; i < delta.length; i++) {
            delta[i] = Math.abs(y - someRes[i]);

        }
    }

    public static void findSumOfInverseCoefficients(int[] delta) {
        for (int i = 0; i < delta.length; i++) {
            sumOfInverseCoefficients += 1.0 / delta[i];
        }
    }

    public static void findProbabilityOfChoosingChromosome(int[] delta) {
        for (int i = 0; i < probabilityOfChoosingChromosome.length; i++) {
            probabilityOfChoosingChromosome[i] = (1.0 / delta[i]) / sumOfInverseCoefficients;
        }
    }

    public static void findPairOfParents() {
        Arrays.sort(probabilityOfChoosingChromosome);
        double num;
        for (int i = 0; i < pairOfParents.length; i++) {
            for (int j = 0; j < pairOfParents[i].length; j++) {
                num = Math.random();
                if (num < probabilityOfChoosingChromosome[0]) {
                    pairOfParents[i][j] = 0;
                }
                if (num >= probabilityOfChoosingChromosome[0] &&
                        num < probabilityOfChoosingChromosome[0] + probabilityOfChoosingChromosome[1]) {
                    pairOfParents[i][j] = 1;
                }
                if (num >= probabilityOfChoosingChromosome[0] + probabilityOfChoosingChromosome[1] &&
                        num < probabilityOfChoosingChromosome[0] + probabilityOfChoosingChromosome[1] + probabilityOfChoosingChromosome[2]) {
                    pairOfParents[i][j] = 2;
                }
                if (num >= probabilityOfChoosingChromosome[0] + probabilityOfChoosingChromosome[1] + probabilityOfChoosingChromosome[2] &&
                        num < probabilityOfChoosingChromosome[0] + probabilityOfChoosingChromosome[1] + probabilityOfChoosingChromosome[2] + probabilityOfChoosingChromosome[3]) {
                    pairOfParents[i][j] = 3;
                } else {
                    pairOfParents[i][j] = 4;
                }
            }
        }
    }

    public static void findChildren(int[][] pairOfParents) {
        int locus = 0;
        for (int i = 0; i < children.length; i++) {
            locus = 1 + (int) (Math.random() * 3);
            if (locus == 1) {
                children[i][0] = rndSolves[pairOfParents[i][0]][0];
                for (int j = locus; j < children[i].length; j++) {
                    children[i][j] = rndSolves[pairOfParents[i][1]][j];
                }
            }
            if (locus == 2) {
                for (int d = 0; d < locus; d++) {
                    children[i][d] = rndSolves[pairOfParents[i][0]][d];
                }

                for (int j = locus; j < children[i].length; j++) {
                    children[i][j] = rndSolves[pairOfParents[i][1]][j];
                }
            }
            if (locus == 3) {
                for (int j = 0; j < children[i].length; j++) {
                    children[i][j] = rndSolves[pairOfParents[i][0]][j];
                }
                children[i][locus] = rndSolves[pairOfParents[i][1]][3];
            }
        }
    }

    public static void mutation(int y){
        for(int i = 0; i < children.length; i++){
            int num = 1 + (int) (Math.random()*100);
            if( num <= 10){
                int mValue = 1 + (int) (Math.random() * y/2);
                int mNum =   (int) (Math.random() * 3);
                children[i][mNum] = mValue;
            }



        }
        for (int i = 0; i < rndSolves.length; i++) {
            for (int j = 0; j <rndSolves[i].length ; j++) {
                rndSolves[i][j] = children[i][j];

            }

        }
    }
}