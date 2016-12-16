package com.weiteng.rxdemo;

import android.animation.ValueAnimator;
import android.content.Context;
import android.os.Bundle;
import android.os.Looper;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
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
import rx.functions.Func2;
import rx.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "MainActivity";

    private Context mContext;
    private Button basicButton;

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
        basicButton = (Button) findViewById(R.id.button_rx_basic);
        basicButton.setOnClickListener(this);
        findViewById(R.id.button_rx_convert).setOnClickListener(this);
        findViewById(R.id.button_rx_just).setOnClickListener(this);
        findViewById(R.id.button_rx_from).setOnClickListener(this);
        findViewById(R.id.button_rx_repeat).setOnClickListener(this);
        findViewById(R.id.button_rx_range).setOnClickListener(this);
        findViewById(R.id.button_rx_timer).setOnClickListener(this);
        findViewById(R.id.button_rx_interval).setOnClickListener(this);
        findViewById(R.id.button_rx_schedule).setOnClickListener(this);
        findViewById(R.id.button_rx_scan).setOnClickListener(this);
        findViewById(R.id.button_rx_merge).setOnClickListener(this);
        findViewById(R.id.button_rx_zip).setOnClickListener(this);
        findViewById(R.id.button_rx_timer1).setOnClickListener(this);
        findViewById(R.id.button_rx_timer2).setOnClickListener(this);
        findViewById(R.id.button_rx_timer3).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_rx_basic:
//                basicDemo();
                animatorDemo();
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

            case R.id.button_rx_schedule: // 线程调度
//                transformThread();
                demo3();
                break;

            case R.id.button_rx_scan:    // Scan操作符
                testScanOfRxJava();
                break;

            case R.id.button_rx_merge:   // Merge操作符
                testMergeOperator();
                break;

            case R.id.button_rx_zip:     // zip操作符
                testZipOperator();
                break;

            case R.id.button_rx_timer1:  // 模拟定时器1
                simulateTimer1();
                break;

            case R.id.button_rx_timer2:  // 模拟定时器2
                simulateTimer2();
                break;

            case R.id.button_rx_timer3:  // 模拟定时器3
                simulateTimer3();
                break;
        }
    }

    void animatorDemo() {
        basicButton.setText("测试动画");

        int distance = 100;
        ValueAnimator animator = ValueAnimator.ofInt(0, distance);
        animator.setTarget(basicButton);
        animator.setDuration(500);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                basicButton.setTranslationY((int)animation.getAnimatedValue());
            }
        });
        animator.start();
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
        Observable.interval(1, TimeUnit.SECONDS)
                .take(20)
                .subscribe(new Action1<Long>() {
                    @Override
                    public void call(Long aLong) {
                        Log.d(TAG, "long = " + aLong);
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        Log.d(TAG, "throwable = " + throwable.getMessage());
                    }
                }, new Action0() {
                    @Override
                    public void call() {
                        Log.d(TAG, "轮询完成");
                    }
                });
    }

    void demo2() {

        Observable<String> observable = Observable.create(new Observable.OnSubscribe<String>() {

            @Override
            public void call(Subscriber<? super String> subscriber) {
                try {
                    Thread.sleep(1000);     // 模拟请求网络
                    String name = "teng";
                    subscriber.onNext(name);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    subscriber.onError(e);
                } finally {
                    subscriber.onCompleted();
                }
            }
        });
        observable.subscribeOn(Schedulers.io())
                .doOnSubscribe(new Action0() {
                    @Override
                    public void call() {
                        toast("onStart()");
                    }
                })
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<String>() {
                    @Override
                    public void call(String s) {
                        toast("onNext() name = " + s);
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        toast(throwable.getMessage());
                    }
                }, new Action0() {
                    @Override
                    public void call() {
                        toast("complete");
                    }
                });
    }

    /**
     * FlitMap 变换
     */
    void demo3() {
        List<Student> students = createStudents();

        Observable
                .from(students)
                .flatMap(new Func1<Student, Observable<Course>>() {

                    @Override
                    public Observable<Course> call(Student student) {
                        List<Course> courses = student.getCourses();
                        return Observable.from(courses);
                    }
                })
                .subscribeOn(Schedulers.io())
                .doOnSubscribe(new Action0() {
                    @Override
                    public void call() {
                        Toast.makeText(mContext, "发射之前", Toast.LENGTH_SHORT).show();
                    }
                })
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        new Action1<Course>() {
                            @Override
                            public void call(Course s) {
                                Toast.makeText(mContext, "name = " + s.name + ", score = " + s.score, Toast.LENGTH_SHORT).show();
                            }
                        }, new Action1<Throwable>() {
                            @Override
                            public void call(Throwable throwable) {
                                Toast.makeText(mContext, throwable.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }, new Action0() {
                            @Override
                            public void call() {
                                Toast.makeText(mContext, "输出完成", Toast.LENGTH_SHORT).show();
                            }
                        });
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
        stu2.setCourses(null);

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

        public Course(String name) {
            this.name = name;
        }

        public Course(String name, int score) {
            this.name = name;
            this.score = score;
        }
    }

    /**
     * 线程的多次切换
     */
    private void transformThread() {
        Observable.just(1, 3, 5)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(new Func1<Integer, String>() {
                    @Override
                    public String call(Integer integer) {
                        Log.d(TAG, "Thread name1 = " + Thread.currentThread().getName());
                        return String.valueOf(integer);
                    }
                })
                .observeOn(Schedulers.io())
                .map(new Func1<String, Course>() {
                    @Override
                    public Course call(String s) {
                        Log.d(TAG, "Thread name2 = " + Thread.currentThread().getName());
                        return new Course(s);
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Course>() {
                    @Override
                    public void call(Course course) {
                        Log.d(TAG, "Thread name subscribe = " + Thread.currentThread().getName());
                        Log.d(TAG, "name = " + course.name);
                    }
                });
    }

    /**
     * Rx性能测试
     */
    public void testScanOfRxJava() {
        List<Integer> list = new ArrayList<>();
        list.add(1);
        list.add(2);
        list.add(3);
        list.add(4);
        Observable
                .from(list)
                .scan(new Func2<Integer, Integer, Integer>() {
                    @Override
                    public Integer call(Integer a, Integer b) {
                        Log.d(TAG, "a = " + a);
                        Log.d(TAG, "b = " + b);
                        return a * b;
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Integer>() {
                    @Override
                    public void call(Integer integer) {
                        Log.d(TAG, "i = " + integer);
                    }
                });
    }

    public void testMergeOperator() {
        List<String> list1 = new ArrayList<>();
        for (int i = 0; i < 30; i++) {
            list1.add("hello i:" + i);
        }
        List<String> list = new ArrayList<>();
        for (int j = 0; j < 30; j++) {
            list.add("world j:" + j);
        }
        List<String> list2 = new ArrayList<>();
        for (int m = 0; m < 30; m++) {
            list2.add("fuck m:" + m);
        }
        Observable<String> world = Observable.from(list);
        Observable<String> hello = Observable.from(list1);
        Observable<String> fuck = Observable.from(list2);

        Observable
                .merge(world, hello, fuck)
                .subscribe(new Action1<String>() {
                    @Override
                    public void call(String s) {
                        Log.d(TAG, "s = " + s);
                    }
                });
    }

    private void testZipOperator() {
        List<String> list1 = new ArrayList<>();
        for (int i = 0; i < 30; i++) {
            list1.add("hello i:" + i);
        }
        List<String> list2 = new ArrayList<>();
        for (int j = 0; j < 30; j++) {
            list2.add("world j:" + j);
        }

        Observable<String> observable1 = Observable.from(list1);
        Observable<String> observable2 = Observable.from(list2);
        Observable
                .zip(observable1, observable2, new Func2<String, String, String>() {
                    @Override
                    public String call(String s1, String s2) {
                        return s1 + " zip " + s2;
                    }
                })
                .subscribe(new Action1<String>() {
                    @Override
                    public void call(String s) {
                        Log.d(TAG, "s = " + s);
                    }
                });

    }

    /**
     * 使用RxJava 模拟定时器
     *
     * 备注：使用 interval 操作符 + take操作符，保留个数
     */
    private void simulateTimer1() {
        Observable
                .interval(1, TimeUnit.SECONDS)
                .take(60)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Long>() {
                    @Override
                    public void call(Long aLong) {
                        int value = aLong.intValue();
                        Log.d(TAG, "time = " + value);
                        Toast.makeText(MainActivity.this, String.valueOf(value), Toast.LENGTH_SHORT).show();
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {

                    }
                }, new Action0() {
                    @Override
                    public void call() {
                        Log.d(TAG, "onCompleted");
                    }
                });
    }

    /**
     * 使用RxJava 模拟定时器
     *
     * 备注：使用 range 操作符 + timer 操作符, 该实现方式有问题，无法实现
     */
    private void simulateTimer2() {
        Observable
                .range(0, 59)
                .timer(1, TimeUnit.SECONDS, AndroidSchedulers.mainThread())
                .subscribe(new Action1<Long>() {
                    @Override
                    public void call(Long aLong) {
                        int value = aLong.intValue();
                        Toast.makeText(MainActivity.this, String.valueOf(value), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    /**
     * 使用RxJava 模拟定时器
     *
     * 备注：使用 range 操作符 + delay 操作符
     */
    private void simulateTimer3() {
        Observable
                .range(0, 59)
                .delay(1, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Integer>() {
                    @Override
                    public void call(Integer value) {
                        Log.d(TAG, "time = " + value);
                        Toast.makeText(MainActivity.this, String.valueOf(value), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
