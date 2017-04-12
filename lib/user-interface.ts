/**
 * Created by ldx on 2017/4/8.
 */
export abstract class UserInterface {
    async abstract makeUserChoose(devices: UIDevice[]): Promise<string> ;
}

export interface UIDevice {
    id: string, name: string
}
