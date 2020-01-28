package com.prakharsaxena.livesaidarshan;

import com.google.firebase.database.Exclude;

public class NewProduct {
 private String ProductName;
 private String ProductQuantity;
 private String ProductTag;
 private String ProductNote;
 private String ProductUrl;
 private String mKey;

 public NewProduct(){


 }

  public NewProduct(String productName, String productQuantity, String productTag, String productNote, String productUrl){
   if(productName.trim().equals("")){
       ProductName="Untitled";
   }else {
       ProductName = productName;
   }
   ProductQuantity=productQuantity;
   ProductTag=productTag;
   ProductNote=productNote;
   ProductUrl=productUrl;

 }
 public NewProduct(String productName, String productQuantity, String productTag, String productNote){
   if(productName.trim().equals("")){
       ProductName="Untitled";
   }else {
       ProductName = productName;
   }
   ProductQuantity=productQuantity;
   ProductTag=productTag;
   ProductNote=productNote;
 }

    public String getProductName() {
        return ProductName;
    }

    public void setProductName(String productName) {
        ProductName = productName;
    }

    public String getProductQuantity() {
        return ProductQuantity;
    }

    public void setProductQuantity(String productQuantity) {
        ProductQuantity = productQuantity;
    }

    public String getProductTag() {
        return ProductTag;
    }

    public void setProductTag(String productTag) {
        ProductTag = productTag;
    }

    public String getProductNote() {
        return ProductNote;
    }

    public void setProductNote(String productNote) {
        ProductNote = productNote;
    }
    public String getProductUrl() {
        return ProductUrl;
    }

    public void setProductUrl(String productUrl) {
        ProductUrl = productUrl;
    }
    @Exclude
    public String getmKey() {
        return mKey;
    }
   @Exclude
    public void setmKey(String mKey) {
        this.mKey = mKey;
    }
}
