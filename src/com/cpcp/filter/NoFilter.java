package com.cpcp.filter;

import java.util.ArrayList;
import java.util.List;

/**
 * A Filter that does nothing.
 */
public class NoFilter extends TextFilter {
   /**
    * @inheritDoc
    */
   public String[] splitFilter(String input) {
      return SmartSplitString.split(input);
   }
}
