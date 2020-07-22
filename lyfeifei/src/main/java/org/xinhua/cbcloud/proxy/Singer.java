package org.xinhua.cbcloud.proxy;

public interface Singer {
    /**
     * 根据歌名点歌
     * @param songName
     */
    public void orderSong(String songName);
    /**
     * 向观众告别
     * @param audienceName
     */
    public void sayGoodBye(String audienceName);
}
