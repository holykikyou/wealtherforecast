package com.example.exp3;

import com.contrarywind.interfaces.IPickerViewData;

import java.util.List;

/**
 * Created by 16838 on 2018/8/1.
 */
public class ShengBean implements IPickerViewData {
    public String name;
    public List<Shi> city;
    public static class Shi{
        public String name;
        public List<String> area;

    }
    //  这个要返回省的名字
    @Override
    public String getPickerViewText() {
        return this.name;
    }
}
