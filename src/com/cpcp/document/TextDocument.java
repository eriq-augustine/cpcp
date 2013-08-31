package com.cpcp.document;

/**
 * A document that handles simple strings.
 */
public class TextDocument implements Document {
   private final String content;

   public TextDocument(String content) {
      this.content = content;
   }

   public String getContent() {
      return content;
   }
}
