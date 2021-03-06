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
package com.helger.as4.servlet.spi;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.w3c.dom.Node;

import com.helger.as4.attachment.WSS4JAttachment;
import com.helger.as4lib.ebms3header.Ebms3UserMessage;
import com.helger.commons.annotation.IsSPIInterface;
import com.helger.commons.collection.ext.ICommonsList;

/**
 * Implement this SPI interface to handle incoming messages appropriate
 *
 * @author Philip Helger
 */
@IsSPIInterface
public interface IAS4ServletMessageProcessorSPI
{
  /**
   * Process incoming AS4 message
   *
   * @param aUserMessage
   *        The received user message. May be <code>null</code>.
   * @param aPayload
   *        Extracted, decrypted and verified payload node (e.g. SBDH). May be
   *        <code>null</code>. May also be <code>null</code> if a MIME message
   *        comes in - in that case the SOAP body MUST be empty and the main
   *        payload can be found in aIncomingAttachments[0].
   * @param aIncomingAttachments
   *        Extracted, decrypted and verified attachments. May be
   *        <code>null</code> or empty if no attachments are present.
   * @return A non-<code>null</code> result object.
   */
  @Nonnull
  AS4MessageProcessorResult processAS4Message (@Nullable Ebms3UserMessage aUserMessage,
                                               @Nullable Node aPayload,
                                               @Nullable ICommonsList <WSS4JAttachment> aIncomingAttachments);
}
