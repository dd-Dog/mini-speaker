package com.flyscale.alertor.data.annotations;

import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @author 高鹤泉
 * @TIME 2020/6/12 10:12
 * @DESCRIPTION 暂无
 */
@IntDef({1,2,3})
@Retention(RetentionPolicy.CLASS)
public @interface noteSendCount {

}
