export interface NFCPlugin {
  echo(options: { value: string }): Promise<{ value: string }>;
  isAvailable(): Promise<{ available: boolean }>;
  scanTag(): Promise<{ tagId: string; data: string }>;
  writeTag(options: { data: string }): Promise<{ success: boolean }>;
}
