package com.weiteng.rxdemo;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Context mContext;
    private Button mButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = this;
        mButton = (Button) findViewById(R.id.button_rx);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        mButton.setOnClickListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
//        demo();
        demo3();
    }

    private void demo() {
        String[] words = new String[]{"yang", "wei", "teng"};
        Observable<String> observable = Observable.from(words);

        Action1<String> onNextAction = new Action1<String>() {

            @Override
            public void call(String s) {
                Toast.makeText(mContext, s, Toast.LENGTH_SHORT).show();
            }
        };

        Action1<Throwable> onErrorAction = new Action1<Throwable>() {

            @Override
            public void call(Throwable throwable) {
                Toast.makeText(mContext, throwable.getMessage(), Toast.LENGTH_SHORT).show();
            }
        };

        Action0 onCompletedAction = new Action0() {

            @Override
            public void call() {
                Toast.makeText(mContext, "输出完成", Toast.LENGTH_SHORT).show();
            }
        };

        observable
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(onNextAction, onErrorAction, onCompletedAction);

    }

    void demo2() {

        Observable<String> observable = Observable.create(new Observable.OnSubscribe<String>() {

            @Override
            public void call(Subscriber<? super String> subscriber) {

            }
        });

        Subscriber<String> subscriber = new Subscriber<String>() {

            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(String s) {

            }
        };

        observable.subscribe(subscriber);
    }

    /**
     * flitMap 变换
     */
    void demo3() {
        List<Student> students = createStudents();
        Observable<Course> observable = Observable
                .from(students)
                .flatMap(new Func1<Student, Observable<Course>>() {

                    @Override
                    public Observable<Course> call(Student student) {
                        List<Course> courses = student.getCourses();
                        return Observable.from(courses);
                    }
                });

        Action1<Course> onNextAction = new Action1<Course>() {

            @Override
            public void call(Course s) {
                Toast.makeText(mContext, "name = " + s.name + ", score = " + s.score, Toast.LENGTH_SHORT).show();
            }
        };

        Action1<Throwable> onErrorAction = new Action1<Throwable>() {

            @Override
            public void call(Throwable throwable) {
                Toast.makeText(mContext, throwable.getMessage(), Toast.LENGTH_SHORT).show();
            }
        };

        Action0 onCompletedAction = new Action0() {

            @Override
            public void call() {
                Toast.makeText(mContext, "输出完成", Toast.LENGTH_SHORT).show();
            }
        };

        observable
                .doOnSubscribe(new Action0() {
                    @Override
                    public void call() {

                    }
                })
                .subscribeOn(Schedulers.io())
                .subscribe(onNextAction, onErrorAction, onCompletedAction);
    }

    List<Student> createStudents() {
        List<Student> students = new ArrayList<>();
        Student stu1 = new Student("zhangsan");
        Student stu2 = new Student("lisi");

        List<Course> courses1 = new ArrayList<>();
        courses1.add(new Course("chinese", 98));
        courses1.add(new Course("math", 88));
        courses1.add(new Course("english", 58));

        List<Course> courses2 = new ArrayList<>();
        courses2.add(new Course("chinese", 56));
        courses2.add(new Course("math", 23));
        courses2.add(new Course("english", 9));

        stu1.setCourses(courses1);
        stu2.setCourses(courses2);

        students.add(stu1);
        students.add(stu2);

        return students;
    }

    class Student {

        private String name;
        private List<Course> courses;

        public Student(String name) {
            this.name = name;
        }

        public Student(String name, List<Course> courses) {
            this.name = name;
            this.courses = courses;
        }

        public void setCourses(List<Course> courses) {
            this.courses = courses;
        }

        public List<Course> getCourses() {
            return courses;
        }

        @Override
        public String toString() {
            return "Student{" +
                    "name='" + name + '\'' +
                    ", courses=" + courses +
                    '}';
        }
    }

    class Course {
        private String name;
        private int score;

        public Course(String name, int score) {
            this.name = name;
            this.score = score;
        }
    }
}
