import { WebPlugin } from '@capacitor/core';

import type { NFCPlugin } from './definitions';

export class NFCWeb extends WebPlugin implements NFCPlugin {
  async echo(options: { value: string }): Promise<{ value: string }> {
    console.log('ECHO', options);
    return options;
  }

  async isAvailable(): Promise<{ available: boolean }> {
    console.log('NFC is not available in web browsers');
    return { available: false };
  }

  async scanTag(): Promise<{ tagId: string; data: string }> {
    throw new Error('NFC scanning is not supported in web browsers');
  }

  async writeTag(_options: { data: string }): Promise<{ success: boolean }> {
    throw new Error('NFC writing is not supported in web browsers');
  }
}
