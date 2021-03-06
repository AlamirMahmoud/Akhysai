package com.ahmed.othman.akhysai.ui.fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.ahmed.othman.akhysai.R;
import com.ahmed.othman.akhysai.RecyclerViewTouchListener;
import com.ahmed.othman.akhysai.adapter.ArticleAdapter;
import com.ahmed.othman.akhysai.pojo.Article;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import static com.ahmed.othman.akhysai.ui.activities.MainActivity.logged_in;
import static com.ahmed.othman.akhysai.ui.activities.MainActivity.navigation_view;
import static com.ahmed.othman.akhysai.ui.activities.MainActivity.shared_pref;
import static com.ahmed.othman.akhysai.ui.activities.MainActivity.toolbar;

public class HomeFragment extends Fragment {

    public HomeFragment() {
        // Required empty public constructor
    }

    Spinner home_search_city, home_search_area, home_search_field, home_search_specialty;
    TextView home_search_button, home_first_title, get_started;
    RecyclerView akhysai_library;
    NestedScrollView nested_scroll;
    boolean isLoggedIn, check_ScrollingUp = false;

    ArrayList<Article> homeArticles = new ArrayList<>();
    public static List<String> Cities = new ArrayList<>(), Areas = new ArrayList<>(), Fields = new ArrayList<>(), Specialties = new ArrayList<>();
    public static View view;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_home, container, false);

        homeArticles = getAllArticles();
        initSpinnerData();

        home_search_city = view.findViewById(R.id.home_search_city);
        home_search_area = view.findViewById(R.id.home_search_area);
        home_search_field = view.findViewById(R.id.home_search_field);
        home_search_specialty = view.findViewById(R.id.home_search_specialty);
        home_search_button = view.findViewById(R.id.home_search_button);
        akhysai_library = view.findViewById(R.id.akhysai_library);
        nested_scroll = view.findViewById(R.id.nested_scroll);
        home_first_title = view.findViewById(R.id.home_first_title);
        get_started = view.findViewById(R.id.get_started);

        toolbar.setVisibility(View.VISIBLE);
        navigation_view.setCheckedItem(R.id.home);

        isLoggedIn = requireActivity().getSharedPreferences(shared_pref, Context.MODE_PRIVATE).getBoolean(logged_in, false);
        if (isLoggedIn) {
            view.findViewById(R.id.home_sign_up_img).setVisibility(View.GONE);
            view.findViewById(R.id.home_sign_up_title).setVisibility(View.GONE);
            view.findViewById(R.id.home_sign_up_body).setVisibility(View.GONE);
            ((TextView) view.findViewById(R.id.num1)).setText("1");
            ((TextView) view.findViewById(R.id.num2)).setText("2");
            ((TextView) view.findViewById(R.id.num3)).setText("3");
        }

        ArrayAdapter<String> city_adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item, Cities);
        city_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        home_search_city.setAdapter(city_adapter);

        home_search_city.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                home_search_area.setVisibility(position > 0 ? View.VISIBLE : View.GONE);
                if (position > 0) {
                    ArrayAdapter<String> area_adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item,
                            position == 1 ? getContext().getResources().getStringArray(R.array.cairo) :
                                    position == 2 ? getContext().getResources().getStringArray(R.array.giza) :
                                            position == 3 ? getContext().getResources().getStringArray(R.array.alex) :
                                                    getContext().getResources().getStringArray(R.array.mansoura));
                    area_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    home_search_area.setAdapter(area_adapter);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        ArrayAdapter<String> field_adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item, Fields);
        field_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        home_search_field.setAdapter(field_adapter);

        home_search_field.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                home_search_specialty.setVisibility(position > 0 ? View.VISIBLE : View.GONE);
                if (position > 0) {
                    ArrayAdapter<String> specialty_adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item,
                            position == 1 ? getContext().getResources().getStringArray(R.array.specialties_medical) :
                                    getContext().getResources().getStringArray(R.array.specialties_with_special_needs));
                    specialty_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    home_search_specialty.setAdapter(specialty_adapter);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        ArticleAdapter articleAdapter = new ArticleAdapter(getContext());

        articleAdapter.setModels(homeArticles);
        akhysai_library.setAdapter(articleAdapter);
        akhysai_library.setLayoutManager(new LinearLayoutManager(getContext()));
        akhysai_library.setHasFixedSize(true);

        akhysai_library.addOnItemTouchListener(new RecyclerViewTouchListener(getContext(), akhysai_library, new RecyclerViewTouchListener.RecyclerViewClickListener() {
            @Override
            public void onClick(View v, int position) {
                Bundle bundle = new Bundle();
                bundle.putString("id", homeArticles.get(position).getArticle_id());
                bundle.putString("article", new Gson().toJson(homeArticles.get(position)));
//                bundle.putParcelable("article", (Parcelable) homeArticles.get(position));
                Navigation.findNavController(v).navigate(R.id.action_homeFragment_to_oneArticleFragment, bundle);
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));

        view.findViewById(R.id.home_third_title).setVisibility(homeArticles.isEmpty() ? View.GONE : View.VISIBLE);

        view.findViewById(R.id.home_sign_up_img).setOnClickListener(v -> Navigation.findNavController(v).navigate(R.id.action_homeFragment_to_loginFragment));
        view.findViewById(R.id.home_sign_up_title).setOnClickListener(v -> Navigation.findNavController(v).navigate(R.id.action_homeFragment_to_loginFragment));
        view.findViewById(R.id.home_sign_up_body).setOnClickListener(v -> Navigation.findNavController(v).navigate(R.id.action_homeFragment_to_loginFragment));


        view.findViewById(R.id.home_search_img).setOnClickListener(v -> Navigation.findNavController(v).navigate(R.id.action_homeFragment_to_searchFragment));
        view.findViewById(R.id.home_search_title).setOnClickListener(v -> Navigation.findNavController(v).navigate(R.id.action_homeFragment_to_searchFragment));
        view.findViewById(R.id.home_search_body).setOnClickListener(v -> Navigation.findNavController(v).navigate(R.id.action_homeFragment_to_searchFragment));

        home_search_button.setOnClickListener(v -> {
            if (home_search_city.getSelectedItemPosition() == 0) {
                home_search_city.setBackground(getActivity().getResources().getDrawable(R.drawable.background_spinner_error));
                home_search_area.setBackground(getActivity().getResources().getDrawable(R.drawable.background_spinner));
                home_search_field.setBackground(getActivity().getResources().getDrawable(R.drawable.background_spinner));
                home_search_specialty.setBackground(getActivity().getResources().getDrawable(R.drawable.background_spinner));
                Toast.makeText(getContext(), "Select city to start search", Toast.LENGTH_SHORT).show();
            } else if (home_search_area.getSelectedItemPosition() == 0) {
                home_search_city.setBackground(getActivity().getResources().getDrawable(R.drawable.background_spinner));
                home_search_area.setBackground(getActivity().getResources().getDrawable(R.drawable.background_spinner_error));
                home_search_field.setBackground(getActivity().getResources().getDrawable(R.drawable.background_spinner));
                home_search_specialty.setBackground(getActivity().getResources().getDrawable(R.drawable.background_spinner));
                Toast.makeText(getContext(), "Select area to start search", Toast.LENGTH_SHORT).show();
            } else if (home_search_field.getSelectedItemPosition() == 0) {
                home_search_city.setBackground(getActivity().getResources().getDrawable(R.drawable.background_spinner));
                home_search_area.setBackground(getActivity().getResources().getDrawable(R.drawable.background_spinner));
                home_search_field.setBackground(getActivity().getResources().getDrawable(R.drawable.background_spinner_error));
                home_search_specialty.setBackground(getActivity().getResources().getDrawable(R.drawable.background_spinner));
                Toast.makeText(getContext(), "Select field to start search", Toast.LENGTH_SHORT).show();
            } else if (home_search_specialty.getSelectedItemPosition() == 0) {
                home_search_city.setBackground(getActivity().getResources().getDrawable(R.drawable.background_spinner));
                home_search_area.setBackground(getActivity().getResources().getDrawable(R.drawable.background_spinner));
                home_search_field.setBackground(getActivity().getResources().getDrawable(R.drawable.background_spinner));
                home_search_specialty.setBackground(getActivity().getResources().getDrawable(R.drawable.background_spinner_error));
                Toast.makeText(getContext(), "Select specialty to start search", Toast.LENGTH_SHORT).show();
            } else {
                Log.w("Bad", home_search_city.getSelectedItemPosition() + "\n" +
                        home_search_area.getSelectedItemPosition() + "\n" +
                        home_search_field.getSelectedItemPosition() + "\n" +
                        home_search_specialty.getSelectedItemPosition() + "\n");
                Bundle bundle = new Bundle();
                bundle.putInt("city", home_search_city.getSelectedItemPosition());
                bundle.putInt("area", home_search_area.getSelectedItemPosition());
                bundle.putInt("field", home_search_field.getSelectedItemPosition());
                bundle.putInt("specialty", home_search_specialty.getSelectedItemPosition());
                Navigation.findNavController(v).navigate(R.id.action_homeFragment_to_searchFragment, bundle);
            }
        });

        get_started.setOnClickListener(v -> nested_scroll.smoothScrollTo(0, home_first_title.getTop()));
        view.findViewById(R.id.home_third_title).setOnClickListener(v -> {
            Navigation.findNavController(v).navigate(R.id.action_homeFragment_to_libraryArticlesFragment);
        });

//        nested_scroll.setOnScrollChangeListener((NestedScrollView.OnScrollChangeListener) (v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
//            if (scrollY > oldScrollY + 2) {
//                // User scrolls down
//                if (!check_ScrollingUp) {
//                    Log.w("UpDown", "DDDDDDDD");
//                    toolbar.startAnimation(AnimationUtils
//                            .loadAnimation(requireContext(), R.anim.trans_upwards));
//                    new Handler().postDelayed(()->toolbar.setVisibility(View.GONE),500);
//                    check_ScrollingUp = true;
//                }
//
//            } else if (scrollY < oldScrollY - 2) {
//
//                // Scrolling up
//                if (check_ScrollingUp) {
//                    Log.w("UpDown", "UUUUUUUUUU");
//                    toolbar.setVisibility(View.VISIBLE);
//                    toolbar.startAnimation(AnimationUtils.loadAnimation(requireContext(), R.anim.trans_downwards));
//                    check_ScrollingUp = false;
//                }
//            }
//
//        });


        return view;
    }

    private void initSpinnerData() {
        Cities = Arrays.asList(getContext().getResources().getStringArray(R.array.cities));
        Areas = Arrays.asList(getContext().getResources().getStringArray(R.array.cairo));
        Fields = Arrays.asList(getContext().getResources().getStringArray(R.array.fields));
        Specialties = Arrays.asList(getContext().getResources().getStringArray(R.array.specialties_with_special_needs));
    }

    private ArrayList<Article> getAllArticles() {
        ArrayList<Article> tempArticles = new ArrayList<>();
        Calendar c1 = Calendar.getInstance();
        c1.add(Calendar.DAY_OF_MONTH, -10);
        Calendar c2 = Calendar.getInstance();
        c2.add(Calendar.DAY_OF_MONTH, -1);
        Calendar c3 = Calendar.getInstance();
        c3.add(Calendar.DAY_OF_MONTH, -142);

        tempArticles.add(new Article("https://www.akhysai.com/upload/Articules/photo-1502086223501-7ea6ecd79368.jpg", "المهارات الحركية الدقيقة", "الطفولة", c1.getTimeInMillis(), "لماذا تعتبر المهارات الحركية الدقيقة مهمة؟ المهارات الحركية الدقيقة ضرورية لأداء المهارات اليومية وكذلك المهارات الأكاديمية. بدون القدرة على إكمال هذه المهام اليومية ، يمكن أن يعاني احترام الطفل لذاته ، ويضعف أداءه الأكاديمي وخيارات لعبه محدودة للغاية. كما أنهم غير قادرين على تطوير الاستقلال المناسب في مهارات \"الحياة\" (مثل ارتداء الملابس وإطعام أنفسهم) والتي لها بدورها آثار اجتماعية ليس فقط داخل الأسرة ولكن أيضًا داخل علاقات الأقران. كيف يمكنك معرفة ما إذا كان الطفل يعاني من صعوبات في المهارات الحركية الدقيقة في لمحة؟ • تجنب و / أو عدم الاهتمام بمهارات الإصبع غير الثابتة • تفضيل النشاط البدني (مرة أخرى لتجنب الجلوس) • الاهتمام بالأنشطة \"السلبية\" مثل تكنولوجيا المعلومات (على سبيل المثال ، مشاهدة التلفزيون و IPAD التي لا تتطلب مهارات حركية دقيقة) • لا اهتمام بمهارات القلم الرصاص أو المقص • أن يكون متسلطًا في اللعب ويطلب من الآخرين \"رسم قطة لي\" • عدم الإصرار على مواجهة التحدي (على سبيل المثال ، مطالبة الآباء بإصلاح المشكلة دون محاولة إصلاحها بأنفسهم) • انتظار أن يلبس الآباء أو ينظفوا أسنانهم بدلاً من تجربة أنفسهم • رفض استخدام القلم مع IPAD كيف يمكنني معرفة ما إذا كان طفلي يعاني من مشاكل في التحكم اليدوي؟ • فهم محرج لقلم الرصاص وضعف التحكم بالقلم الرصاص. • صعوبة الإمساك بالمقص والتلاعب به. • الميل لاستخدام يدهم بالكامل للتعامل مع شيء ما بدلاً من مجرد أصابع قليلة. • ضعف التحمل للأنشطة القائمة على قلم الرصاص والمقص. • صعوبة في مهام الرعاية الذاتية ، مثل ربط أربطة الحذاء والقيام بالأزرار. • صعوبة الإمساك وإطلاق الأشياء بطريقة مضبوطة. • فوضوي و / أو كتابة بخط اليد بطيئة. • صعوبة البقاء داخل الخطوط عند تلوين الأشكال أو قطعها. • صعوبة فتح الأوعية أو فتح الأغطية. ما هي المشاكل الأخرى التي يمكن أن تحدث عندما يواجه الطفل صعوبات في التحكم في اليد؟ - المهارات الحركية الدقيقة: مهارات الإصبع واليد مثل الكتابة والقص وفتح صناديق الغداء وربط أربطة الحذاء. - المهارات الحركية الكبري : المهارات الجسدية للجسم بالكامل باستخدام عضلات القوة الأساسية للجذع والذراعين مثل ألعاب المضارب والكرة. • السلوك: مثل تجنب المهام الصعبة للتحكم في اليد في الرعاية الذاتية أو الأكاديمية أو حتى اللعب. • المثابرة في المهمة: قد تكون المثابرة محدودة في مواجهة المهام الصعبة. • هيمنة اليد: الاستخدام الثابت ليد واحدة (عادة ما تكون واحدة) لأداء المهام التي تسمح بتطوير المهارات المحسنة. • التكامل الثنائي: استخدام اليدين مع اليد الأمامية (على سبيل المثال ، استخدام اليد المسيطرة للإمساك بالقلم الرصاص وتحريكه ولكن غير المسيطر لحمل ورقة الكتابة). • الرعاية الذاتية: المهام اليومية التي يتم القيام بها لتكون جاهزة للمشاركة في أنشطة الحياة (بما في ذلك خلع الملابس وتناول الطعام وتنظيف الأسنان وفتح وإغلاق صناديق الغداء أو حزم الختم). ما الذي يمكن فعله لتحسين التحكم اليدوي؟ ما هي الأنشطة التي يمكن أن تساعد في تحسين التحكم اليدوي؟ • الشطب وتمزيق الورق . • استخدام الملقط لالتقاط الأشياء الصغيرة (مثل الخرز). • التقط الأشياء باستخدام الإبهام والسبابة والإصبع الأوسط مع إبقاء الأصابع الأخرى مطوية في راحة اليد. • أنشطة لعب الأدوار التي تنطوي على البرم باليدين أو بالشابة/ مرققات العجين \"rolling pin\" ، وإخفاء الأشياء مثل العملات المعدنية في اللعب أو مجرد البناء الإبداعي. • عجن العجين. • عصر الإسفنج أو الألعاب المائية في الحمام. • خيوط الخرز على الخيط. • الأنشطة اليومية التي تتطلب قوة مثل فتح الحاويات والجرار والزجاجات. • مشاريع المقص التي تنطوي على قطع الأشكال الهندسية ثم لصقها معًا لعمل صور مثل الروبوتات أو القطارات أو المنازل. لماذا يجب أن أبحث عن العلاج إذا لاحظت صعوبات في التحكم اليدوي في طفلي؟ يعد التدخل العلاجي لمساعدة الطفل المصاب بصعوبات التحكم في اليد مهمًا من أجل: • تحسين قدرة الطفل ، والمثابرة في المهام الحركية الدقيقة المطلوبة لكل يوم من الحياة وكذلك النجاح الأكاديمي. • ساعد الطفل على إكمال مهام الرعاية الذاتية المناسبة للعمر ، مثل عمل الأزرار والسحابات. • تجنب انفصال الطفل في بيئة أكاديمية بسبب صعوبات إكمال الكتابة والرسم و / أو قطع الأنشطة. • تجنب الإحباط الذي يعاني منه الآباء والمعلمون والأطفال عندما يكافح الطفل للبقاء منخرطًا في الأنشطة الأكاديمية. • ساعد في الحفاظ على شعور إيجابي بالرفاهية وتطويره بسبب الثقة في مهارات ما قبل المدرسة والمدرسة واللعب مع الأقران واستقلالية الرعاية الذاتية. • تأكد من أن الطفل لا يتخلف عن أقرانه في تطوير خط اليد - وهو سبب رئيسي لضعف الأداء الأكاديمي. • تأكد من تطوير التحكم بالماوس لمهام تقنية المعلومات (حيث يتم دفع الأطفال ذوي مهارات القلم الرصاص الضعيفة نحو تكنولوجيا المعلومات كوسيلة اتصال مكتوبة بديلة). إذا تركت دون معالجة ما الذي يمكن أن تؤدي إليه صعوبات التحكم في اليد؟ عندما يواجه الأطفال صعوبات في التحكم اليدوي ، فقد يواجهون أيضًا صعوبات في: • تلبية المعايير الأكاديمية بسبب ضعف مهارات الكتابة اليدوية والتعب السريع. • الضغط والقلق المفرط لدى طفل في سن المدرسة بسبب صعوبات \"المواكبة\" في الفصل. • إكمال الامتحانات بسبب صعوبة الإجابة عن جميع الأسئلة المكتوبة خلال الوقت المحدد. • ضعف احترام الذات عندما يقارن الطفل قدرات مهارة الإصبع (مثل الكتابة والقص) مع أقرانه. • تطوير مهارات الكتابة الفعالة والتحكم في الماوس للكمبيوتر الشخصي أو القلم لأجهزة iPad. • معالجة العناصر للبناء (مثل الألغاز ، ليغو Lego\" المكعبات \"). د/ محمد صالح", ""));
        tempArticles.add(new Article("https://www.akhysai.com/upload/Articules/pexels-photo-3933030.jpeg", "السلوك النمطى التكرارى", "ذوى الاحتياجات الخاصة", c2.getTimeInMillis(), "- السلوك النمطي التكراريStereotypical Repetitive Behavior يشير السلوك المتكرر عموما بصفة عامة إلى مجموعة واسعة من السلوكيات بما في ذلك النمطية والسلوك الجامد ، والدوافع ، والهواجس ، والمحافظة ، التكرار والنمطية في استخدام اللغة (Watt et al., 2008). • ومن أكثر أنواع السلوك المضطرب لدى هؤلاء الأطفال هو تكرار الأفعال أو القيام بأعمال نمطية مثل سلوك الاهتزاز (هز الجسم إلى الأمام وإلى الخلف أثناء الجلوس)، والدوران حول النفس، والتلويح بالذراعين، والهمهمة وترديد ثلاث أو أربع كلمات أو مجل معينة لفترة طويلة من الوقت (القاسم؛ عبيد؛ الزغبي، 2001، 128-130). ويعرف (أسامة فاروق ، 2015) السلوك النمطي التكراري بانة هو مجموعة من السلوكيات التي تتضمن السلوك النمطي ، والسلوك الجامد ، والدوافع ، والهواجس ، والمحافظة ، والتكرار والنمطية في استخدام اللغة. ويؤثر السلوك المتكرر والمقيد على التفاعل بين الطفل والأسرة ، وأثبتت الدراسات أن هذا السلوك عندما يكون في مرحلة متطورة له آثاره المدمرة علي التأثير الوظيفي للأسرة و الأطفال ذوى اضطراب التوحد. (Klin et al., 2007) وتظهر أنماط من السلوك النمطي في الأنشطة التي يؤديها ، وفي اهتماماته . وهذه الأنماط تشمل الانشغال بواحدة أو أكثر من الأنماط المقيدة للسلوك النمطي وتمسكه غير المرن بأعمال محددة أو طقوس ، أو الانشغال بأجزاء من الموضوعات .(فاروق ، 2014-ب ،282 ) إن هذه الأنماط السلوكية المختلفة للطفل ذوى اضطراب التوحد ، ربما يكون لها وظائف معينة فمثال على ذلك النشاط فقد يكون ممتع بحد ذاته لهم ، أو يكون ناتج عن ضغط شديد وقلق. فانهم يظهرون نوع من الهدوء والاطمئنان عندما يعرفون أي نشاط سيقومون به في الحياة اليومية ، أما عندما يفاجئون بنشاط جديد دون معرفتهم المسبقة له فإنهم يغضبون وربما يرجع ذلك إلي قصور الإدراك ، وقدرتهم المحدودة في معالجة المعلومات الجديدة . وتذكر الشامي (2004 ، 374 – 375 )، الأسباب التي تؤدى بالأشخاص الأطفال ذوى اضطراب التوحد الى ممارسة سلوكيات نمطية متكررة تتمثل في الآتي:- 1- التخفيف من شحنة مثيرات يصعب عليهم تحملها. أن السلوكيات النمطية المتكررة تحدث عندما يتعرض الطفل التوحدي لشحنة ضخمة من المثيرات البيئية دون أن يتمكن من علاجها. 2- الحركات السلوكية هي مصدر متعة للشخص الذي يمارسها. أن الطفل قد يعكس أحاسيس ممتعة، وأن هذه الأحاسيس الممتعة تبقي الطفل منهمكا بذلك النوع من السلوك، ولا يكون سببها جذب انتباه الآخرين. متي تحدث السلوكيات النمطية التكرارية لدى الأطفال ذوى اضطراب التوحد: -في أوضاع السعادة. -في أوضاع سكون النفس. -في أوضاع التوتر والقلق . (الشامي ،2004 ، 377 ) السلوك النمطي التكراري وعلاقته بالقصور في العلاقات الاجتماعية: يوجد اقتراح أن السلوكيات المتكررة هي وسيلة للانسحاب من المواقف الاجتماعية التي يصعب فهمها ، هذه الفرضية تفسر الإصرار علي تماثل السلوك كمحاولة لكسب السيطرة علي البيئة وتفسير أن الاهتمامات المحددة تكون اهتمامات غير اجتماعية مما يحدث نتيجة القصور في الوظائف الاجتماعية. عندما يتم منع الأطفال اضطراب التوحد عن السلوك المتكرر والمقيد مما يسبب لهم صعوبات نفسية وسلوكية (Klin et al., 2007) إن السلوك المتكرر والمقيد يؤثر سلبيا علي التعلم الشامل ، والتنمية الاجتماعية، وتكيف الفرد مع الكبار والأقران ، وأفراد الأسرة وفي مرحلة ما قبل المدرسة يقضي الأطفال معظم وقتهم في السلوك المتكرر والمقيد مما يجعل الطفل أكثر فقرا على التكيف في هذه المرحلة.(Zandt et al., 2007). السلوك المتكرر والمقيد يؤثر على التفاعل بين الطفل والأسرة ، وأثبتت الدراسات أن هذا السلوك عندما يكون في مرحلة متطورة له آثار مدمرة على التأثير الوظيفي للأسرة والطفل (South et al. ,2005). كيفية خفض السلوك النمطي التكراري لدى اضطراب التوحد : 1- تعليم السلوك البديل ، والتزود بمختلف الخبرات الحسية أثناء النوم. 2- عندما يحدث السلوك حاول جذب انتباه الطفل لشيء آخر. 3- الخفض التدريجي لمقدار الوقت المرتبط بالسلوك ، زيادة مقدار الوقت بين الأوقات المجدوله لسلوكيات التكرارية. 4- استخدام السلوك التكرارى لتحديد مستوى الضغوط لدى الطفل. 5- وزع بين المهمات التي يعرفها الطفل التي لا يعرفها (صعب- سهل). 6- أضف عنصراً ممتعاً إلى الخطة التعليمية ، ووازن بين المتعة ومهمات التعليم. 7- لا تكثر من استخدام المعلومات اللفظية، لا تطلب منهم كمية كبيرة من الإجابات اللفظية. 8- ضرورة إشغال وقت فراغ الطفل بألعاب وبرامج هادفة من مثل الرياضة ، والفن ، والسباحة ، ومشاهدة التلفاز. 9- إبعاد الأشياء التي يقوم بها بشكل متكرر باستخدامها ، أو بتدويرها وذلك بالتقليل التدريجي. 10- إشراك الطفل بالأعمال المنزلية بتكليفه بترتيب الغرفة أو بنقل بعض الأشياء البسيطة من مكان إلى آخر. 11- تشجيع الطفل على اللعب الجماعي مع الأقران بدلاً من العزلة واللعب الفردي ، ومن أمثلة اللعب الجماعي (السلم والثعبان ، وشد الحبل ، والكرة ، والمسابقات).(فاروق ، الشربيني ، 2013 ،158-159 ) أ . د / اسامة فاروق", ""));
        tempArticles.add(new Article("https://www.akhysai.com/upload/Articules/pexels-photo-4715142.jpeg", "الترويح العلاجى لذوى الشلل الدماغى", "ذوى الاحتياجات الخاصة", c3.getTimeInMillis(), "لترويح العلاجي لذوي الشلل الدماغي يركز العلاج الترويحيي ، المعروف أيضًا باسم الترويح العلاجي- therapeutic recreation- ، على تصميم الطرق التي يمكن للفرد من خلالها المشاركة الكاملة في الأنشطة الترويحيية التي يختارونها. يعمل المعالجون الترويحييون Recreation therapists على تحديد مستوى الاهتمام والقدرات والأساليب التكيفية ، وفي بعض الحالات العمليات المعدلة المطلوبة لإكمال بنجاح. يحسن الاندماج في الأنشطة المعززة للحياة والخبرات البدنية والعقلية والاجتماعية للطفل. #ما هو العلاج الترويحيي؟ لدينا جميعاً أوقات ممتعة نستمتع بها. بالنسبة للأطفال المصابين بالشلل الدماغي ، يجب ألا يكون هذا مختلفًا. إن أنشطتنا ، سواء كانت السباحة أو لعب كرة السلة ، أو حب الرسم أو الرقص ، توفر لنا ساعات من الترفيه. حيث يعزز الاستجمام قدرتنا على التواصل مع الآخرين والانخراط في البيئة من حولنا. لذلك ، يعتبر العلاج الترويحيي - المعروف أيضًا باسم الترويح العلاجي - خطوة مهمة في مساعدة الفرد المصاب بالشلل الدماغي على أن يصبح فردًا جيدًا يمنح الفوائد التي توفرها التجارب البدنية والعقلية والاجتماعية. يركز العلاج الترويحيي على الإدماج ، وليس الاستبعاد ، من خلال السماح للفرد بالمشاركة وأن يكون جزءًا لا يتجزأ من الأنشطة التي يتمتعون بها ويتعلمون منها. ومع ذلك ، فإن العلاج الترويحيي له أيضًا هدف آخر - تعزيز قدرة الطفل المصاب بالشلل الدماغي على التخطيط للمهام واستراتيجياتها وتنفيذها في محاولة لتحقيق الأداء البدني المحسن وتشجيع الرفاهية العاطفية من خلال تسهيل الاندماج في الأنشطة التي يستفيدون بها ويستمتعون بها . هذا يوفر جودة الحياة. العلاج الترويحيي هو علاج يساعد الأطفال المصابين بالشلل الدماغي على تطوير وتوسيع القدرات البدنية والمعرفية أثناء المشاركة في الأنشطة الترويحيية. على الرغم من أن الطفل قد يشارك في العلاجات الأخرى التي تعالج على وجه التحديد الحاجة إلى الوظيفة الجسدية ، فقد تم تصميم العلاج الترويحيي خصيصًا للسماح للأطفال بالمشاركة في الأنشطة الترويحيية عن طريق إزالة الحواجز التي تعوق ممارسة الرياضة والفنون والحرف اليدوية والألعاب وغيرها من الأنشطة المعززة للحياة . تنطبق عبارة \"#حيث_توجد_إرادة_، #هناك_طريقة\" على العلاج الترويحيي. عندما يواجه الأطفال ذوي الإعاقة عقبة أمام أداء نشاط يحسن الحياة ، يعمل المعالجون التروحيون على تحديد مستوى الاهتمام والقدرات والأساليب التكيفية ، وفي بعض الحالات العمليات المعدلة المطلوبة لإكمال النشاط بنجاح. تقدمت فرص الترفيه على مر السنين. تم تعديل الرياضات مثل الرجبي وكرة القدم والتنس للأفراد على الكراسي المتحركة. يمكن لعب الهوكي باستخدام رياضات مبتكرة مصممة خصيصًا للزلاجات المتطرفة ، مثل سباق الدراجات المنحدر المعدل ، تثبت أن الأشخاص ذوي الإعاقة لديهم خيارات رياضية أكثر - وحدود أقل - من أي وقت مضى. يعالج الترويح العلاجي أيضًا الفنون والأنشطة الثقافية. يمكن للأطفال الذين يعانون من قوة اليد الحركية الدقيقة المعرضة للخطر استخدام الطين أكثر نعومة من الطين العادي لصنع الأواني الخزفية. تعليمات الرسم المنظمة باستخدام الألوان النابضة بالحياة يمكن أن تساعد الأطفال على بناء اتصالات عصبية حاسمة. يمكن إنشاء العمل الفني باستخدام التطبيقات المتخصصة والمعدات المساعدة. يشارك الأطفال في حفلات الرقص باستخدام المعدات التكيفية والحركات المعدلة والنغمة المقبولة. #تتضمن أهداف العلاج الترويحيي ما يلي: تحديد قدرة الطفل على الأداء الترويحيي تقليل إعاقة الطفل من خلال تعليمه استراتيجيات التكيف تحفيز الطفل على المشاركة في الأنشطة بتشجيع ودعم تعديل الادوات والإجراءات لتعزيز الإدماج توسيع قدرة الطفل على الاختلاط وتكوين صداقات تعزيز مفهوم الطفل لذاته وثقته بنفسه مساعدة الطفل على تنمية اهتماماته #ما هي فوائد العلاج الترويحيي؟ العلاج الترويحيي له فوائد عديدة للأطفال المصابين بالشلل الدماغي - يمكنه تحسين الوظائف الجسدية ، وتحسين الروابط العصبية المرتبطة بأنشطة المعالجة ، وتوفير فرص للاندماج. يستفيد الأطفال المشاركون في العلاج الترويحيي داخل البيئات الجماعية أو الانفرادية. يقلل الوقت الذي يقضيه في أنشطة مفيدة من فرص الاكتئاب والوحدة والإحباط. في الواقع ، يوفر العلاج الترويحيي إحساسًا أكبر بقيمة الذات والإنجاز. فائدة أخرى سيستمتع بها الطفل هي القدرة على المشاركة في الأنشطة مع عائلته ، وأطفال الحي ، وزملائه في المدرسة وغيرهم ممن لديهم اهتمامات مماثلة. عندما يتم قبول الفرد في مجموعة ، يتعرض الأعضاء الآخرون في تلك المجموعة إلى القدرات المتزايدة باستمرار للأشخاص الذين يعانون من ضعف. غالبًا ما يرغب الأشقاء والأصدقاء وزملاء العمل في التفاعل مع الأفراد المصابين بالشلل الدماغي ، ولكنهم يشعرون بالإحباط بسبب قدرتهم على معرفة الكيفية. غالبًا ما يصبح أولئك المدربون في مجال الترفيه الذين يشاركون في الأنشطة أمثلة على الإمكانية البشرية والفهم والإلهام لمن يعانون من ضعف أو لا. تشمل الفوائد البدنية للعلاج الترويحيي ما يلي: تحسين البراعة الجسدية زيادة القوة والمرونة تحسين اللياقة البدنية والصحة تحسن البراعة الرياضية تحسين التنسيق تشمل الفوائد النفسية للعلاج الترويحيي ما يلي: قبول الإعاقة زيادة المهارات الاجتماعية زيادة القدرة على التحكم في التوتر والاكتئاب انخفاض الغضب والقلق تقلص العزلة الاجتماعية تحسين صورة الجسم تحسين الرفاهية والاسترخاء تشمل الفوائد المعرفية للعلاج الترويحيي ما يلي: سلوك محسن ومقبول إجتماعياً زيادة المهارات التحليلية وصنع القرار تحسين الثقة زيادة التنظيم زيادة الإدراك متى ينصح بالعلاج الترويحيي؟ ينصح المتخصصون بالعلاج الترويحيي في بيئة تعليمية خاصة في مدرسة الطفل ؛ من خلال المستشارين السلوكيين الذين يعملون على بناء احترام الطفل لذاته ؛ أو من قبل الآباء المعنيين الذين يبحثون عن فرص معززة لأطفالهم. لا يوجد عمر محدد يبدأ فيه العلاج الترويحيي أو ينتهي. ومع ذلك ، يجب أن تكون الأساليب الترويحيية التي يتم إدخالها مناسبة دائمًا للعمر. من المرجح أن يتعرف الأطفال الصغار على الرياضات الخفيفة وأنشطة الملاعب والألعاب والفنون. مع تقدم الأطفال في السن ، يمكن تقديم مناهج ترفيهية أكثر تنظيماً ورياضات جماعية وأنشطة إبداعية معقدة مثل الدراما والموسيقى. عندما يكون في سن المراهقة ، قد يرغب الأطفال في المشاركة في مجال اهتمام مثل الجولف أو السباحة أو الغوص أو الرقص أو اللعب المدرسي أو عروض المواهب أو المنافسات.. وينصب التركيز على توسيع الاهتمامات والقدرة على معرفة الفرص بمجرد التفكير في عدم توفرها يمكن تعديلها لاستيعابها. يمكن أن يكون هذا ممكناً.سيساعد الفريق الطبي للطفل المعالج الترويحيي في استنباط تدخلات ترفيهية تلبي احتياجات علاج الطفل وترضي اهتماماته. كيف يتم تنفيذ العلاج الترويحيي؟ يستفيد العلاج الترويحيي من التدخلات القائمة على النشاط والمتأصلة في الأداء الوظيفي والتواصل والسلوك والتكيف والتعديل للظروف المادية والعمليات المعرفية. خطط العلاج فردية للغاية ؛ قد لا تتطابق التقنيات التي تعمل على طفل بسهولة إلى طفل آخر. يجب على المعالجين الترويحييين تحديد أي افضل طرق جسدية وعاطفية بشكل منهجي ؛ يستكشفون التعديلات ، وعند الاقتضاء ، التعديلات. تختلف عمليات التكيف بشكل واضح عن التعديلات في أن عمليات التكيف قد تتطلب مساعدة الأجهزة التقويمية أو المعدات التكيفية أو التقنيات المساعدة ، بينما تعد التعديلات طرقًا بديلة لأداء الطفل لنشاط ما. يمكن أن تؤدي النتيجة المرجوة إلى وجود قيود يمكن إدارتها أو عدم وجودها ، مما يتيح المشاركة الكاملة وفرص الإدماج الناجحة. قد يكون اتخاذ خطوة في الأنشطة الترويحيية أمرًا مرهقًا للأطفال المصابين بالشلل الدماغي ؛ تم تصميم العديد من التقنيات التي يستخدمها المعالجون لتحفيز الأطفال ؛ إنها وظيفة المعالج لغرس الثقة في الطفل. قد يكون من المحبط أن يتعلم الطفل طرقًا جديدة لأداء أو التغلب على العقبات. ومع ذلك ، عندما يواجه الطفل فائدة قبوله وإدراجه في المجالات التي تهمه ، غالبًا ما يقبل الأطفال ويتعلمون بسرعة ، خاصة إذا كانت بيئة التعلم إيجابية وداعمة. تشمل التدخلات المستخدمة ما يلي: تدريب بدني محدد في الأنشطة إعادة التدريب المعرفي التدريبات الصحية Wellness training استراتيجيات إدارة الإجهاد الاستشارة السلوكية مهارات اللعب التنشئة الاجتماعية ولعب دور المحادثة واحد لواحد تفاعلات وأنشطة المجموعة الصغيرة الإندماج في المجتمع التخيل الموجه والتأمل الاسترخاء والارتجاع البيولوجي أين يتم إجراء العلاج الترويحيي؟ يتم إجراء العلاج الترويحيي في عدة مواقع ، والعديد منها ذو إعدادات سريرية clinical settings. ومع ذلك ، نظرًا لأن العلاج الترويحيي يعتمد على قدرات الطفل واهتماماته ، يمكن أن يتم العلاج في الداخل indoors أو في الهواء الطلق ، أو في أي مكان حيث يشارك الفرد في أنشطة توفر المتعة والترفيه. سيوجه المعالجون أيضًا أفراد العائلة حول كيفية الانخراط في أنشطة ترويحية مع أطفالهم في المنزل أو في الأماكن العامة. في بعض الأحيان ، قد يكون المعالج الترويحيي في متناول يده لمساعدة الآخرين في تعلم كيفية تضمين واحتضان جهود الطفل المعاق ، مثل الفصول الدراسية والصالات الرياضية وبرامج المجتمع. في بعض الحالات ، يتم تسجيل الطفل في برامج مصممة خصيصًا للآخرين الذين يعانون من ضعف مماثل ، وبالتالي بمجرد تدريبهم يتم دمجهم مع الآخرين الذين تغلبوا أيضًا على عقباتهم في المشاركة. تشمل الأماكن الداخلية indoors: المنزل المدارس المكاتب الطبية مرافق الصحة النفسية مراكز العيش المستقل مراكز العيادات الخارجية مراكز إعادة التأهيل مرافق التمريض الماهرة المراكز المهنية مراكز المجتمع مرافق الترفيه العامة والخاصة تشمل المواقع الخارجية outdoors: الحدائق ملاعب مراكز المجتمع من يقدم العلاج الترويحيي؟ يتم توفير العلاج الترويحيي من قبل متخصصين مدربين ومعتمدين لتمكين الأطفال المعاقين. يحصل معظم المعالجين على درجة البكالوريوس في العلاج الترويحيي ، لكن الآخرين يحصلون على درجات في مجالات الدراسة الأخرى المتعلقة بالصحة. من المرجح أن يكون المعالجون قد أكملوا أيضًا مجموعة من التدريبات العملية كجزء من تعليمهم. الدورات الدراسية المطلوبة للمعالجين الترويحييين تشمل: تشريح تقييم وتقويم العملاء الإسعافات الأولية والسلامة ديناميكيات المجموعة التنمية البشرية علم الحركة المصطلحات الطبية علم وظائف الأعضاء علم النفس علم الاجتماع ماذا يحدث أثناء العلاج الترويحيي؟ عادة ما يشعر الآباء بالارتياح لمعرفة أن هناك الكثير من الفرص لأطفالهم للمشاركة. يتم تمكين الأطفال من خلال الاحتمالات ، خاصة إذا كان بإمكانهم التواصل مع أقرانهم. لتضمين الطفل - عادةً طفل يعاني من إعاقة جسدية أكثر شدة - يتم تقييم قدرات الطفل واهتماماته ومزاجه واحتياجاته التكيفية من قبل المعالج. يتشاور المعالجون مع طبيب الرعاية الأولية للطفل لتنسيق خطط العلاج. كما يتشاورون مع أخصائيي العلاج الطبيعي والوظيفي لدى الطفل للتأكد من مستويات القدرة التي تم تحقيقها بالفعل. يحلل المعالجون أيضًا فريق دعم الطفل ، الروتين اليومي والمهارات الاجتماعية. سيقوم المعالج الترويحيي بعد ذلك بتحليل كامل للأداء البدني والمعرفي المطلوب لأداء الأنشطة المهمة ، بما في ذلك المتطلبات البدنية للتنقل والوظيفة ، والعملية المعرفية للتعلم ، والمعالجة واتخاذ القرار ، وكذلك الطلب العاطفي للأداء ، التفاعل والمنافسة. يمكن دمج طرق التكيف والتعديل ، إذا لزم الأمر. قد يشمل التقييم: مراجعة السجلات الطبية الملاحظة في محيط سريري الاختبارات المعيارية التعاون مع أعضاء الفريق الطبي الآخرين بعد ذلك يحدد المعالج خطة العلاج. قبل تنفيذ الخطة ، يجب على المعالج تحديد عدة عوامل ، بما في ذلك: ما إذا كانت الخطة تلبي اهتمامات وأهداف الطفل كيف تعزز ميكانيكا جسم الطفل النشاط أو تعوقه ما هي المعدات المساعدة ، إن وجدت؟ إذا كان الطفل يحتاج إلى استراتيجيات تكيف إضافية إذا كان الطفل يحتاج إلى مهارات اجتماعية محسنة للأنشطة الجماعية كيفية دمج أنظمة الدعم الإيجابية أثناء العلاج قد تستخدم خطة العلاج العديد من الاستراتيجيات لمساعدة الطفل على تطوير حلول للفجوات الجسدية والمعرفية والاجتماعية. على سبيل المثال ، قد يتم وضع طفل خجول أو غير مرتاح في المواقف الاجتماعية في أنشطة جماعية أكثر بحيث تكون لديه الفرصة لبناء مهارات التواصل الاجتماعي إلى جانب إتقان النشاط. سيعلم المعالجون أيضًا الأطفال كيفية استخدام أي معدات متخصصة مطلوبة للأنشطة ، وسيعلمون الأطفال طرقًا بديلة للمشاركة في الأنشطة. قد يشمل ذلك تعليم الطفل كيفية وضع نفسه بشكل صحيح في أنشطة معينة ، وكيفية تسريع أنشطة فرض الضرائب الجسدية مثل الرياضة ، وكيفية التنفس طوال العملية. يجب على المعالجين أيضًا تحديد وإبلاغ الأطفال ومقدمي الرعاية ببرامج موارد المجتمع المحلي ووسائل النقل التي يمكن أن تعزز فرصهم للترفيه خارج النطاقات العلاجية والمدرسية. قد يكون لديهم إمكانية الوصول إلى مصادر التمويل التي قد تكون مطلوبة لتغطية نفقات تتجاوز تلك التي قد يغطيها التأمين الصحي. يعرض المعالجون أيضًا للوالدين ومقدمي الرعاية كيفية صيانة المعدات وتنظيفها بشكل صحيح. في بعض الأنشطة ، يجب ارتداء المعدات واستخدامها بطريقة لا تضر بالطفل. يتم توفير إرشادات السلامة. طوال فترة العلاج ، سيقيّم المعالج الترويحيي باستمرار مستوى مشاركة الطفل وتقدمه ، وسيجري تغييرات على خطة علاج الطفل حسب الحاجة. هل هناك اعتبارات خاصة أو مخاطر للعلاج الترويحيي؟ كما هو الحال مع جميع الأنشطة - وخاصة الرياضة - ينطوي الأمر على مستوى معين من المخاطر. ومع ذلك ، عندما يتم علاج الطفل من قبل معالج مؤهل نفذ خطة علاج مدروسة جيدًا ، يتم تقليل المخاطر والإشراف عليها باستمرار ومراقبتها حتى يتم الحصول على مستويات السلامة.", ""));

        return tempArticles;
    }

}