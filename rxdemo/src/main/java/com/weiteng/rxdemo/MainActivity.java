package com.weiteng.rxdemo;

import android.content.Context;
import android.os.Bundle;
import android.os.Looper;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = this;
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
        findViewById(R.id.button_rx_basic).setOnClickListener(this);
        findViewById(R.id.button_rx_convert).setOnClickListener(this);
        findViewById(R.id.button_rx_just).setOnClickListener(this);
        findViewById(R.id.button_rx_from).setOnClickListener(this);
        findViewById(R.id.button_rx_repeat).setOnClickListener(this);
        findViewById(R.id.button_rx_range).setOnClickListener(this);
        findViewById(R.id.button_rx_timer).setOnClickListener(this);
        findViewById(R.id.button_rx_interval).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_rx_basic:
                basicDemo();
                break;

            case R.id.button_rx_convert:
                convertDemo();
                break;

            case R.id.button_rx_just:
                justDemo();
                break;

            case R.id.button_rx_from:
                fromDemo();
                break;

            case R.id.button_rx_repeat:
                repeatDemo();
                break;

            case R.id.button_rx_range:
                rangeDemo();
                break;

            case R.id.button_rx_timer:
                timerDemo();
                break;

            case R.id.button_rx_interval: // 轮询
                intervalDemo();
                break;
        }
    }

    /**
     * 最基本的创建方式
     */
    void basicDemo() {
        // 使用create 创建被观察者/被订阅者
        Observable<String> observable = Observable.create(new Observable.OnSubscribe<String>() {

            @Override
            public void call(Subscriber<? super String> subscriber) {
                subscriber.onStart();
                subscriber.onNext("yangweiteng");
                subscriber.onCompleted();
            }
        });

        /*
        // 创建观察者/订阅者
        Observer<String> observer = new Observer<String>() {

            @Override
            public void onCompleted() {
                toast("completed");
            }

            @Override
            public void onError(Throwable e) {
                toast("onError(): e = " + e.getMessage());
            }

            @Override
            public void onNext(String s) {
                toast("completed");
            }
        };
        */

        // 使用Subscribe快速创建观察者
        Subscriber<String> subscriber = new Subscriber<String>() {

            @Override
            public void onStart() {
                super.onStart();
                toast("onStart()");
            }

            @Override
            public void onNext(String s) {
                toast(s);
            }

            @Override
            public void onCompleted() {
                toast("completed");
            }

            @Override
            public void onError(Throwable e) {
                toast("onError(): e = " + e.getMessage());
            }
        };

        // 被观察者订阅观察者
        observable.subscribe(/*observer*/subscriber);
    }

    /**
     * 使用接口函数快速创建
     */
    void convertDemo() {
        // 使用create 创建被观察者/被订阅者
        Observable<String> observable = Observable.create(new Observable.OnSubscribe<String>() {

            @Override
            public void call(Subscriber<? super String> subscriber) {
                subscriber.onStart();
                subscriber.onNext("yangweiteng");
                subscriber.onCompleted();
            }
        });

        // 接口函数快速创建
        Action1<String> onNextAction = new Action1<String>() {

            @Override
            public void call(String s) {
                toast(s);
            }
        };

        Action1<Throwable> onErrorAction = new Action1<Throwable>() {

            @Override
            public void call(Throwable throwable) {
                toast("onError(): e = " + throwable.getMessage());
            }
        };

        Action0 onCompletedAction = new Action0() {

            @Override
            public void call() {
                toast("completed");
            }
        };

        observable.subscribe(onNextAction, onErrorAction, onCompletedAction);
    }

    /**
     * Just快速创建
     */
    void justDemo() {
        Observable.just("yang", "wei", "teng", "is", "a", "android", "beginner")
                .subscribe(
                        new Action1<String>() {
                            @Override
                            public void call(String s) {
                                toast(s);
                            }
                        },
                        new Action1<Throwable>() {
                            @Override
                            public void call(Throwable throwable) {
                                toast(throwable.getMessage());
                            }
                        },
                        new Action0() {
                            @Override
                            public void call() {
                                toast("completed");
                            }
                        }
                );
    }

    /**
     * From快速创建
     */
    void fromDemo() {
        String[] names = {"yang", "wei", "teng", "is", "a", "android", "beginner"};
        Observable.from(names)
                .subscribe(
                        new Action1<String>() {
                            @Override
                            public void call(String s) {
                                toast(s);
                            }
                        },
                        new Action1<Throwable>() {
                            @Override
                            public void call(Throwable throwable) {
                                toast(throwable.getMessage());
                            }
                        },
                        new Action0() {
                            @Override
                            public void call() {
                                toast("completed");
                            }
                        }
                );
    }

    /**
     * 使用 Repeat 继续构建
     */
    void repeatDemo() {
        Observable.just("yang", "wei", "teng")
                .repeat(2)                      // 重复两次
                .subscribe(
                        new Action1<String>() {
                            @Override
                            public void call(String s) {
                                toast(s);
                            }
                        },
                        new Action1<Throwable>() {
                            @Override
                            public void call(Throwable throwable) {
                                toast(throwable.getMessage());
                            }
                        },
                        new Action0() {
                            @Override
                            public void call() {
                                toast("completed");
                            }
                        }
                );
    }

    /**
     * 使用数字范围构建
     */
    void rangeDemo() {
        // 从一个指定的数字 x 开始发射 n 个数字[x, x + n - 1]
        Observable.range(10, 3)
                .subscribe(new Action1<Integer>() {
                    @Override
                    public void call(Integer integer) {
                        toast("number is " + integer);
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        toast("error occur " + throwable.getMessage());
                    }
                });
    }

    void timerDemo() {
        // 如果你需要一个一段时间之后才发射的Observable，你可以像下面的例子使用timer()：
        Observable.timer(1, TimeUnit.MICROSECONDS, AndroidSchedulers.mainThread())
                .subscribe(new Action1<Long>() {
                    @Override
                    public void call(Long aLong) {
                        toast("time is " + aLong);
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        Looper.prepare();
                        toast("error occur " + throwable.getMessage());
                    }
                });
        // 3秒后调用观察者，完成事件
    }

    void intervalDemo() {
        // 需要一个重复执行的Observable
        Observable.interval(2, TimeUnit.MICROSECONDS, AndroidSchedulers.mainThread())
                .take(10)           // 取用前10个
                .subscribe(new Action1<Long>() {
                    @Override
                    public void call(Long aLong) {
                        toast("time is " + aLong);
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        toast("error occur " + throwable.getMessage());
                    }
                });

        // 两次间隔执行的时间是 2 秒
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

    private void toast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
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
