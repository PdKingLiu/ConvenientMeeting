package com.pdking.convenientmeeting.weight;

import com.pdking.convenientmeeting.db.AllUserBean;

import java.util.List;

/**
 * @author liupeidong
 * Created on 2019/5/11 17:27
 */
public interface AddMultiMemberListener {
    void addMemberCallBack(List<AllUserBean.DataBean> checkedBean);
}
