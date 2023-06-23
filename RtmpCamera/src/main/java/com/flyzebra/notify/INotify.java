/**
 * FileName: INotify
 * Author: FlyZebra
 * Email:flycnzebra@gmail.com
 * Date: 2023/6/23 10:23
 * Description:
 */
package com.flyzebra.notify;

public interface INotify {

   void notify(byte[] data, int size);

   void handle(int type, byte[] data, int size, byte[] params);

}
