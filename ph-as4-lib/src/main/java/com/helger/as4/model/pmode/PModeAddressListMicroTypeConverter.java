/**
 * Copyright (C) 2015-2017 Philip Helger (www.helger.com)
 * philip[at]helger[dot]com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.helger.as4.model.pmode;

import com.helger.commons.collection.ext.CommonsArrayList;
import com.helger.commons.collection.ext.ICommonsList;
import com.helger.xml.microdom.IMicroElement;
import com.helger.xml.microdom.MicroElement;

public class PModeAddressListMicroTypeConverter extends AbstractPModeMicroTypeConverter
{
  private static final String ELEMENT_ADDRESSES = "Addresses";

  public IMicroElement convertToMicroElement (final Object aObject, final String sNamespaceURI, final String sTagName)
  {
    final PModeAddressList aValue = (PModeAddressList) aObject;
    final IMicroElement ret = new MicroElement (sNamespaceURI, sTagName);
    for (final String sEncrypt : aValue.getAllAddresses ())
    {
      ret.appendElement (sNamespaceURI, ELEMENT_ADDRESSES).appendText (sEncrypt);
    }
    return ret;
  }

  public Object convertToNative (final IMicroElement aElement)
  {
    final ICommonsList <String> aAddresses = new CommonsArrayList<> ();
    for (final IMicroElement aEncryptElement : aElement.getAllChildElements (ELEMENT_ADDRESSES))
    {
      aAddresses.add (aEncryptElement.getTextContentTrimmed ());
    }

    return new PModeAddressList (aAddresses);
  }

}
