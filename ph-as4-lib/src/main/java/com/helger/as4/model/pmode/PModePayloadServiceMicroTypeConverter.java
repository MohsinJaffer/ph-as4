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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.helger.as4.attachment.EAS4CompressionMode;
import com.helger.xml.microdom.IMicroElement;
import com.helger.xml.microdom.MicroElement;
import com.helger.xml.microdom.convert.IMicroTypeConverter;

public final class PModePayloadServiceMicroTypeConverter implements IMicroTypeConverter
{
  private static final String ATTR_COMPRESSION_MODE = "CompressionMode";

  @Nonnull
  public IMicroElement convertToMicroElement (@Nonnull final Object aObject,
                                              @Nullable final String sNamespaceURI,
                                              @Nonnull final String sTagName)
  {
    final PModePayloadService aValue = (PModePayloadService) aObject;
    final IMicroElement ret = new MicroElement (sNamespaceURI, sTagName);
    ret.setAttribute (ATTR_COMPRESSION_MODE, aValue.getCompressionModeID ());
    return ret;
  }

  @Nonnull
  public PModePayloadService convertToNative (@Nonnull final IMicroElement aElement)
  {
    final String sCompressionModeID = aElement.getAttributeValue (ATTR_COMPRESSION_MODE);
    final EAS4CompressionMode eCompressionMode = EAS4CompressionMode.getFromIDOrNull (sCompressionModeID);
    if (sCompressionModeID != null && eCompressionMode == null)
      throw new IllegalStateException ("Invalid compression mode ID '" + sCompressionModeID + "' provided!");

    return new PModePayloadService (eCompressionMode);

  }
}
