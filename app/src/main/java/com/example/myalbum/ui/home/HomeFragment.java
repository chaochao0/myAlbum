package com.example.myalbum.ui.home;

import static androidx.recyclerview.widget.RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myalbum.MainActivity;
import com.example.myalbum.R;
import com.example.myalbum.data.AndroidPhotoScanner;
import com.example.myalbum.data.PhotoItem;
import com.example.myalbum.database.GsonInstance;
import com.example.myalbum.database.Image;
import com.example.myalbum.database.ImageRepository;
import com.example.myalbum.databinding.FragmentHomeBinding;
import com.example.myalbum.ui.face.FaceViewModel;
import com.example.myalbum.utils.DateUtil;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private static HomeViewModel homeViewModel = null;
    static public boolean scanButtonIsAvailable = true;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        if(homeViewModel == null)
            homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        setHasOptionsMenu(true);


//        //监听RecyclerView滚动状态
//        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
//            @Override
//            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
//                super.onScrollStateChanged(recyclerView, newState);
//                if(recyclerView.getLayoutManager() != null) {
//                    getPositionAndOffset();
//                }
//            }
//        });

//        homeViewModel.getPhotoWithDay().observe(getViewLifecycleOwner(), new Observer<LinkedHashMap<String, List<PhotoItem>>>() {
//            @Override
//            public void onChanged(LinkedHashMap<String, List<PhotoItem>> stringListLinkedHashMap) {
//                RecyclerView recyclerView = binding.rvHome;
//
//                RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getContext(), 4);
//                recyclerView.setLayoutManager(layoutManager);
//                RecyclerViewAdapter adapter = new RecyclerViewAdapter(getContext(), stringListLinkedHashMap);
//                //调用这个函数的时候SpacePhoto并不是空的
//                recyclerView.setAdapter(adapter);
//
//            }
//        });
        homeViewModel.getImageList().observe(getViewLifecycleOwner(), new Observer<List<Image>>() {
            @Override
            public void onChanged(List<Image> images) {
                System.out.println("homeViewModelGetImageListOnChanged"+": images length"+images.size());
                LinkedHashMap<String, List<PhotoItem>> mSectionsOfDay = new LinkedHashMap<>();
//                images.get(0).printInfo();
////                Date date = new Date(images.get(images.size()-1).getModified() * 1000);
////                String detail = AndroidPhotoScanner.mDataFormatOfDay.format(date);
////                String week = DateUtil.getWeek(date);
////                String dayKey = detail + week;
//                int i =0;
//                for(Image image:images) {
//                    for (Image image2 : images) {
//                        if (image.date == image2.date && image.imageId != image2.imageId) {
//                            image.printInfo();
//                            image2.printInfo();
//                        }
////                    System.out.println("date: "+image.date);
////                        image.printInfo();
//                    }
//                }
                for(Image image:images){
                    PhotoItem photo = new PhotoItem(image.path,image.date);

                    Date date = new Date(photo.getModified() * 1000);
                    String detail = AndroidPhotoScanner.mDataFormatOfDay.format(date);
                    String week = DateUtil.getWeek(date);
                    String dayKey = detail + week;
                    if(!mSectionsOfDay.containsKey(dayKey)) {
                        List<PhotoItem> section = new ArrayList<>();
                        section.add(photo);
                        mSectionsOfDay.put(dayKey, section);
                    } else {
                        List<PhotoItem> section = mSectionsOfDay.get(dayKey);
                        section.add(photo);
                    }
                }
//                for(Map.Entry<String, List<PhotoItem>> entry: mSectionsOfDay.entrySet()) {
//                    Log.i("photoWithDay",entry.getKey());
//                    for(PhotoItem item:entry.getValue()){
//                        Log.i("path",item.getPath());
//                    }
//                }

                RecyclerView recyclerView = binding.rvHome;

                RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getContext(), 4);
                recyclerView.setLayoutManager(layoutManager);
                RecyclerViewAdapter adapter = new RecyclerViewAdapter(getContext(), mSectionsOfDay);
                //调用这个函数的时候SpacePhoto并不是空的
                recyclerView.setAdapter(adapter);
            }
        });



//        final TextView textView = binding.textHome;
//        homeViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return root;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){
        super.onCreateOptionsMenu(menu,inflater);
//        MenuInflater inflater =getActivity().getMenuInflater();
//
//        inflater.inflate(R.menu.title_with_button, menu);
        menu.getItem(0).setTitle("扫描");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        if(scanButtonIsAvailable)
        {
            switch (item.getItemId()) {
                case R.id.action_cart://监听菜单按钮
                    System.out.println("扫描点击事件");
                    new AlertDialog.Builder(getContext()).setTitle("提示")//设置对话框标题
                            .setMessage("扫描照片更新数据库需要一定时间，是否确定扫描")
                            .setPositiveButton("是", new DialogInterface.OnClickListener() {//添加确定按钮
                                @Override
                                public void onClick(DialogInterface dialog, int which) {//确定按钮的响应事件，点击事件没写，自己添加
                                    ImageRepository.getImageRepositoryInstance().restartScanPhoto();
                                    scanButtonIsAvailable = false;
                                }
                            }).setNegativeButton("否", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    }).show();//在按键响应事件中显示此对话框
                    break;
            }
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onDestroyView() {
        Log.i("HomeFragmentOnDestroyVIew","start");
        super.onDestroyView();
        binding = null;
    }

    /*
     *  保存离开Fragment时的浏览位置
     */
//    @Override
//    public void onSaveInstanceState(Bundle outState) {
//        super.onSaveInstanceState(outState);
//
//        int lastPosition = 0;//RecyclerView的第一个可视item的position
//        int lastOffset = 0;//与RecyclerView顶部的偏移量
//
//        if(layoutManager==null)
//            layoutManager = new GridLayoutManager(getContext(), 4);
//
//        //返回当前RecycelrView中第一个可见的item的位置
//        View topView = layoutManager.getChildAt(0);
//        if (topView != null) {
//            lastOffset = topView.getTop();
//            lastPosition = layoutManager.getPosition(topView);
//        }
//
//        outState.putInt("lastOffset",lastOffset);
//        outState.putInt("lastPosition",lastPosition);
//
//    }

    /**
     * 记录RecyclerView当前位置
//     */
//    private void getPositionAndOffset() {
//        //获取可视的第一个view
//        View topView = layoutManager.getChildAt(0);
//        if(topView != null) {
//            //获取与该view的顶部的偏移量
//            homeViewModel.lastOffset = topView.getTop();
//            //得到该View的数组位置
//
//            homeViewModel.lastPosition = layoutManager.getPosition(topView);
//            Log.i("getPositionAndOffset",String.valueOf(homeViewModel.lastPosition));
//        }
//    }
//
//    /**
//     * 让RecyclerView滚动到指定位置
//     */
//    private void scrollToPosition() {
//        if(recyclerView.getLayoutManager() != null && homeViewModel.lastPosition >= 0) {
//            recyclerView.getLayoutManager().scrollToPosition(homeViewModel.lastPosition);
//        }
//    }

}