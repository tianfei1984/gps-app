package cn.com.gps169.bos.model;

import cn.com.gps169.db.model.Terminal;

/**
 * 终端封装类
 * @author tianfei
 *
 */
public class TerminalVo extends Terminal {
    private int vid;

    /**
     * @return the vid
     */
    public int getVid() {
        return vid;
    }

    /**
     * @param vid the vid to set
     */
    public void setVid(int vid) {
        this.vid = vid;
    }
}
