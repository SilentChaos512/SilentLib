package net.silentchaos512.lib.util;

import java.util.ArrayList;
import java.util.List;

@Deprecated
public class CollectionUtils {

  // NO
  public static <T> List<T> asMutableList(T... array) {

    List<T> list = new ArrayList<>();
    for (int i = 0; i < array.length; ++i)
      list.add(array[i]);
    return list;
  }
}
